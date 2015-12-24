/*
 * The SignalK developers license this file to you under the
 * Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nz.co.fortytwo.signalk.util;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TrackSimplifierTest {

    @Test
    public void combinedSimplification() {
        List<Position> track = ImmutableList.<Position>builder()
                .add(new Position(0.0, 0.0))
                .add(new Position(0.05, 0.05)) // eliminated by vertex reduction
                .add(new Position(0.5, 0.6)) // eliminated by dp reduction
                .add(new Position(1.0, 1.0))
                .add(new Position(2.0, 0.95))  // eliminated by dp reduction
                .add(new Position(2.9, 1.0))
                .add(new Position(3.0, 1.0)) // eliminated by vertex reduction
                .build();
        List<Position> expected = ImmutableList.of(new Position(0.0, 0.0), new Position(1.0, 1.0), new Position(2.9, 1.0));
        assertEquals(expected, TrackSimplifier.simplify(track, 0.25));
    }

    @Test
    public void vertexReductionIgnoresSinglePoint() {
        List<Position> track = ImmutableList.of(new Position(1.0, 1.0));
        assertSame(track, TrackSimplifier.vertexReduction(track, 0.5));
    }

    @Test
    public void vertexReductionCollapsesAdjacentPoints() {
        List<Position> track = ImmutableList.<Position>builder()
                .add(new Position(0.0, 0.0))
                .add(new Position(0.1, -0.1))
                .add(new Position(0.8, 0.9))
                .add(new Position(1.0, 1.0))
                .build();
        List<Position> expected = ImmutableList.of(new Position(0.0, 0.0), new Position(0.8, 0.9));
        assertEquals(expected, TrackSimplifier.vertexReduction(track, 0.5));
    }

    @Test
    public void dpReductionSinglePoint() {
        List<Position> track = ImmutableList.<Position>builder()
                .add(new Position(0.0, 0.0))
                .add(new Position(0.5, 0.6))
                .add(new Position(1.0, 1.0))
                .build();
        List<Position> expected = ImmutableList.of(new Position(0.0, 0.0), new Position(1.0, 1.0));
        assertEquals(expected, TrackSimplifier.dpReduction(track, 0.25));
    }

    @Test
    public void dpReductionMultiplePoints() {
        List<Position> track = ImmutableList.<Position>builder()
                .add(new Position(0.0, 0.0))
                .add(new Position(0.1, 0.05))
                .add(new Position(0.5, 0.6))
                .add(new Position(0.85, 0.8))
                .add(new Position(1.0, 1.0))
                .build();
        List<Position> expected = ImmutableList.of(new Position(0.0, 0.0), new Position(1.0, 1.0));
        assertEquals(expected, TrackSimplifier.dpReduction(track, 0.25));
    }

    @Test
    public void dpReductionMultipleSegments() {
        List<Position> track = ImmutableList.<Position>builder()
                .add(new Position(0.0, 0.0))
                .add(new Position(0.5, 0.6))
                .add(new Position(1.0, 1.0))
                .add(new Position(2.0, 0.95))
                .add(new Position(3.0, 1.0))
                .build();
        List<Position> expected = ImmutableList.of(new Position(0.0, 0.0), new Position(1.0, 1.0), new Position(3.0, 1.0));
        assertEquals(expected, TrackSimplifier.dpReduction(track, 0.25));
    }
}

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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PositionTest {

    @Test
    public void properties() {
        Position base = new Position(2.0, 1.0);
        assertEquals(1.0, base.longitude(), 0.0);
        assertEquals(2.0, base.latitude(), 0.0);
    }

    @Test
    public void hashingAndEquality() {
        Position c1 = new Position(2.0, 1.0);
        Position c2 = new Position(2.0, 1.0);
        assertTrue(c1.equals(c2));
        assertTrue(c2.equals(c1));
        assertTrue(c1.hashCode() == c2.hashCode());

        Position c3 = new Position(1.0, 2.0);
        assertFalse(c1.equals(c3));
        assertFalse(c3.equals(c1));
        assertFalse(c1.hashCode() == c3.hashCode());
    }

    @Test
    public void stringFormat() {
        assertEquals("3.5 N, 2.5 W", new Position(3.5, -2.5).toString());
        assertEquals("3.5 S, 2.5 E", new Position(-3.5, 2.5).toString());
        assertEquals("0.0 N, 0.0 E", new Position(0.0, 0.0).toString());
    }
}
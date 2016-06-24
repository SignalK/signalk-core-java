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

import java.util.ArrayList;
import java.util.List;

/**
 * Utility funciton for simplifying a list of positions.
 */
public class TrackSimplifier {

    /**
     * Simplify a track by eliminating intermediate positions.
     *
     * @param track     the track
     * @param tolerance the tolerance factor
     * @return a simplified track
     */
    public static List<Position> simplify(List<Position> track, double tolerance) {
        return dpReduction(vertexReduction(track, tolerance), tolerance);
    }

    static List<Position> vertexReduction(List<Position> track, double tolerance) {
        // degenerate case
        if (track.size() < 2) {
            return track;
        }

        ArrayList<Position> result =new ArrayList<Position>();
        double tol2 = tolerance * tolerance;

        Position last = track.get(0);
        result.add(last);
        for (int i = 1; i < track.size(); i++) {
            Position current = track.get(i);

            if (distanceSquared(last, current) > tol2) {
                result.add(current);
                last = current;
            }
        }
        return result;
    }

    static List<Position> dpReduction(List<Position> track, double tolerance) {
        // degenerate case
        if (track.size() < 3) {
            return track;
        }

        double tol2 = tolerance * tolerance;
        boolean[] marks = new boolean[track.size()];
        marks[0] = true;
        marks[marks.length - 1] = true;
        mark(track, tol2, 0, track.size() - 1, marks);

        ArrayList<Position> result = new ArrayList<>();
        for (int i = 0; i < marks.length ; i++ ) {
            if (marks[i]) {
                result.add(track.get(i));
            }
        }
        return result;
    }

    private static void mark(List<Position> track, double tol2, int start, int end, boolean[] marks) {
        int furthest = findFurthest(track, tol2, start, end);
        if (furthest > 0) {
            marks[furthest] = true;
            mark(track, tol2, start, furthest, marks);
            mark(track, tol2, furthest, end, marks);
        }
    }

    private static int findFurthest(List<Position> track, double tol2, int start, int end) {
        if (start + 1 >= end) {
            return -1;
        }

        Segment segment = new Segment(track.get(start), track.get(end));
        int mark = -1;
        double maxDistance2 = -1.0;

        for (int i = start + 1; i < end; i++) {
            Position position = track.get(i);
            double distance2 = segment.distance2(position);
            if (distance2 > maxDistance2) {
                mark = i;
                maxDistance2 = distance2;
            }
        }
        if (maxDistance2 > tol2) {
            return mark;
        }

        return -1;
    }

    private static class Segment {
        private final Position p0;
        private final Position p1;
        private final Position v;
        private final double c2;

        public Segment(Position p0, Position p1) {
            this.p0 = p0;
            this.p1 = p1;
            v = new Position(p1.latitude() - p0.latitude(), p1.longitude() - p0.longitude());
            c2 = dot(v, v);
        }

        public double distance2(Position p) {
            Position w = new Position(p.latitude() - p0.latitude(), p.longitude() - p0.longitude());
            double c1 = dot(w, v);
            if (c1 < 0) {
                return distanceSquared(p, p0);
            }
            if (c1 >= c2) {
                return distanceSquared(p, p1);
            }
            return distanceSquared(p, pb(c1));
        }

        private Position pb(double c1) {
            double b = c1 / c2;
            return new Position(p0.latitude() + b * v.latitude(), p0.longitude() + b * v.longitude());
        }

        private static double dot(Position a, Position b) {
            return a.latitude() * b.latitude() + a.longitude() * b.longitude();
        }
    }

    private static double distanceSquared(Position a, Position b) {
        double dx = b.longitude() - a.longitude();
        double dy = b.latitude() - a.latitude();
        return dx * dx + dy * dy;
    }
}

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

import java.util.Objects;

/**
 * A position expressed as latitude and longitude.
 */
public class Position {

    private final double longitude;
    private final double latitude;
    // TODO: Do we also want altitude and if so should it used for equality?

    /**
     * Basic constructor.
     */
    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the latitude coordinate. Positive values indicate North.
     */
    public double latitude() {
        return latitude;
    }

    /**
     * Returns the longitude. Positive values indicate East.
     */
    public double longitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position that = (Position) o;
        return Double.compare(that.latitude, latitude) == 0 && Double.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (latitude >= 0.0) {
            b.append(latitude).append(" N");
        } else {
            b.append(-latitude).append(" S");
        }
        b.append(", ");
        if (longitude >= 0.0) {
            b.append(longitude).append(" E");
        } else {
            b.append(-longitude()).append(" W");
        }
        return b.toString();
    }
}

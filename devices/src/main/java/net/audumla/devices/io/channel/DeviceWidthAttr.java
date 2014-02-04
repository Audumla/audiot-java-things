package net.audumla.devices.io.channel;

/*
 * *********************************************************************
 *  ORGANIZATION : audumla.net
 *  More information about this project can be found at the following locations:
 *  http://www.audumla.net/
 *  http://audumla.googlecode.com/
 * *********************************************************************
 *  Copyright (C) 2012 - 2013 Audumla.net
 *  Licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *  You may not use this file except in compliance with the License located at http://creativecommons.org/licenses/by-nc-nd/3.0/
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 *  "AS IS BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceWidthAttr implements DeviceChannel.Attribute {
    private static final Logger logger = LoggerFactory.getLogger(DeviceWidthAttr.class);

    public enum DeviceWidth {WIDTH8, WIDTH16, WIDTH32, WIDTH864}

    protected DeviceWidth width;

    public DeviceWidthAttr(DeviceWidth width) {
        this.width = width;
    }

    public DeviceWidth getWidth() {
        return width;
    }

    public void setWidth(DeviceWidth width) {
        this.width = width;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceWidthAttr that = (DeviceWidthAttr) o;
        return width == that.width;
    }

    @Override
    public int hashCode() {
        return width != null ? width.hashCode() : 0;
    }
}

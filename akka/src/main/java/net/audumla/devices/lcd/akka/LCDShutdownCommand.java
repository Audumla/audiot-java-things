package net.audumla.devices.lcd.akka;

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

import net.audumla.akka.CommandEvent;
import net.audumla.devices.lcd.CharacterLCD;

public class LCDShutdownCommand implements CommandEvent<CharacterLCD> {


    public LCDShutdownCommand() {
    }

    @Override
    public boolean execute(CharacterLCD lcd) {
//		lcd.clear();
//		lcd.write("System shutdown");
        return false;
    }

}

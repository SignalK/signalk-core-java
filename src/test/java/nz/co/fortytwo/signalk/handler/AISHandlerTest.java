/*
 *
 * Copyright (C) 2012-2014 R T Huitema. All Rights Reserved.
 * Web: www.42.co.nz
 * Email: robert@42.co.nz
 * Author: R T Huitema
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package nz.co.fortytwo.signalk.handler;

import static nz.co.fortytwo.signalk.util.SignalKConstants.self;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import nz.co.fortytwo.signalk.model.SignalKModel;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.junit.Test;

//public class AISHandlerTest extends CamelTestSupport {
public class AISHandlerTest{

	private static Logger log = LogManager.getLogger(AISHandlerTest.class);
	/*
	@EndpointInject(uri = "mock:result")
	protected MockEndpoint resultEndpoint;

	@Produce(uri = "direct:start")
	protected ProducerTemplate template;
*/
	
	
	@Test
	public void shouldPassJson() throws Exception{
		 String jStr = "{\"vessels\":{\""+self+"\":{\"environment\":{\"wind\":{\"angleApparent\":0.0000000000,\"directionTrue\":0.0000000000,\"speedApparent\":0.0000000000,\"speedTrue\":20.0000000000}}}}}";
		 AISHandler processor = new AISHandler();
		
		 SignalKModel model =  processor.handle(jStr);
		 log.debug(model);
		 assertNull(model);
	}
	@Test
	//@Ignore
	public void shouldIgnoreSingleMessage() throws Exception{
		String msg = "$PGHP,1,2010,6,11,11,46,11,929,244,0,,1,72*21\r\n";
		msg += "\\1G2:0125,c:1354719387*0D";
		msg += "\\!AIVDM,2,1,4,A,539LiHP2;42`@pE<000<tq@V1<TpL4000000001?1SV@@73R0J0TQCAD,0*1E\r\n";
		msg += "\\2G2:0125*7B";
		msg += "\\!AIVDM,2,2,4,A,R0EQCP000000000,2*45";
		AISHandler processor = new AISHandler();
		SignalKModel model =  processor.handle(msg);
		 log.debug(model);
		 assertNull(model);

	}

	@Test
	//@Ignore
	public void shouldParseSingleMessage() throws Exception{
		String msg = "!AIVDM,1,1,,B,15MwkRUOidG?GElEa<iQk1JV06Jd,0*6D";
		AISHandler processor = new AISHandler();
		SignalKModel model =  processor.handle(msg);
		assertNotNull(model);
		
		log.debug(msg);
		log.debug(model);
	}

	@Test
	//@Ignore
	public void shouldIgnoreSeparatedMessage() throws Exception{

		String msg1 = "$PGHP,1,2013,3,13,10,39,18,375,219,,2190047,1,4A*57\r\n";
		String msg2 = "\\g:1-2-0136,c:1363174860*24\\!BSVDM,2,1,4,B,53B>2V000000uHH4000@T4p4000000000000000S30C6340006h00000,0*4C\r\n";
		String msg3 = "\\g:2-2-0136*59\\!BSVDM,2,2,4,B,000000000000000,2*3A";
		AISHandler processor = new AISHandler();
		SignalKModel model = processor.handle(msg1);
		assertNull(model);

		model = processor.handle(msg2);
		assertNull(model);

		model = processor.handle(msg3);
		assertNull(model);
		// assertEquals(map.get("TEST"), 5);
	}

	@Test
	//@Ignore
	public void shouldParseTwoMessages() throws Exception{

		String msg1 = "!AIVDM,1,1,,A,15MvJw5P0NG?Us6EaDVTTOvR06Jd,0*22";
		AISHandler processor = new AISHandler();
		SignalKModel model = processor.handle(msg1);
		assertNotNull(model);
		//assertTrue(map.get(Constants.AIS) instanceof AisVesselInfo);

		String msg = "!AIVDM,1,1,,B,15Mtu:0000o@05tE`?Ctn@6T06Jd,0*40";
		model=null;
		model = processor.handle(msg);
		assertNotNull(model);
		//assertTrue(map.get(Constants.AIS) instanceof AisVesselInfo);
	}

	@Test
	//@Ignore
	public void shouldIgnoreTwoMessages() throws Exception{

		String msg1 = "$PGHP,1,2013,3,13,10,39,18,375,219,,2190047,1,4A*57\r\n";
		String msg2 = "\\g:1-2-0136,c:1363174860*24\\!BSVDM,2,1,4,B,53B>2V000000uHH4000@T4p4000000000000000S30C6340006h00000,0*4C\r\n";
		String msg3 = "\\g:2-2-0136*59\\!BSVDM,2,2,4,B,000000000000000,2*3A";
		AISHandler processor = new AISHandler();
		SignalKModel model = processor.handle(msg1);
		assertNull(model);

		model = processor.handle(msg2);
		assertNull(model);

		model = processor.handle(msg3);
		assertNull(model);
		model=null;
		String msg = "$PGHP,1,2010,6,11,11,46,11,929,244,0,,1,72*21\r\n";
		msg += "\\1G2:0125,c:1354719387*0D";
		msg += "\\!AIVDM,2,1,4,A,539LiHP2;42`@pE<000<tq@V1<TpL4000000001?1SV@@73R0J0TQCAD,0*1E\r\n";
		msg += "\\2G2:0125*7B";
		msg += "\\!AIVDM,2,2,4,A,R0EQCP000000000,2*45";

		model = processor.handle(msg);
		assertNull(model);
		// assertEquals(map.get("TEST"), 5);
	}

}

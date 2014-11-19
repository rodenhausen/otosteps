package edu.arizona.biosemantics.oto2.oto.shared.log;

import edu.arizona.biosemantics.common.log.AbstractLogInjection;
import edu.arizona.biosemantics.common.log.ILoggable;

public aspect LogInjection extends AbstractLogInjection {
	
	declare parents : edu.arizona.biosemantics.oto2.oto.server..* implements ILoggable;
}

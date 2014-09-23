package com.github.onsdigital;

import com.github.davidcarboni.restolino.framework.Home;
import com.github.davidcarboni.restolino.helpers.HomeRedirect;

public class Index extends HomeRedirect implements Home {

	public Index() {
		super("/index.html");
	}

}

package com.github.onsdigital.json.taxonomy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.generator.Folder;
import com.github.onsdigital.json.TaxonomyHome;

public class T3 extends TaxonomyHome {

	public URI headline;
	public List<URI> items = new ArrayList<>();
	public URI statsBulletinHeadline;
	public List<URI> statsBulletins = new ArrayList();
	public List<URI> datasets = new ArrayList();


	public T3(Folder folder) {
		super(folder);
		level = "t3";
		lede = "Lorem ipsum dolor sit amet, fugit tation percipitur duo at, magna denique ei duo, mazim oratio volumus id mel. Veri discere accusamus sit id, oblique fabulas legimus sit ex. "
				+ "Atqui urbanitas efficiendi ei sea. Voluptua gloriatur vis an, cu eius semper deleniti vel. Aperiri insolens accommodare vel ne, vel id dictas corrumpit urbanitas.";
		more = "Eam cu alia nostrud. In movet epicuri quo. Decore reprimique vix ei. Ea sit possim albucius, an has mutat lucilius inimicus. Vis et vocibus voluptatum, ne est unum meliore invidunt. "
				+ "Posse nemore vim ei, habeo omnesque ullamcorper usu at, eu eos cetero accusata consulatu.";
		headline = URI.create("/economy/inflationandpriceindices/timeseries/d7bt");
		items.add(headline);
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/d7g7"));
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/l522"));
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/l55o"));
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/chaw"));
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/czbh"));
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/kvr8"));
		items.add(URI.create("/economy/inflationandpriceindices/timeseries/kvr9"));

		statsBulletinHeadline = URI.create("/economy/inflationandpriceindices/bulletins/consumerpriceinflation");
		statsBulletins.add(URI.create("/economy/inflationandpriceindices/bulletins/consumerpriceinflation"));
		statsBulletins.add(URI.create("/economy/inflationandpriceindices/bulletins/housepriceindex"));

		datasets.add(URI.create("/economy/inflationandpriceindices/datasets/inflationsummary"));
		datasets.add(URI.create("/economy/inflationandpriceindices/datasets/producerpricesindex"));

	}

}

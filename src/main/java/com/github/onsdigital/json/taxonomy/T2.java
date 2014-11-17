package com.github.onsdigital.json.taxonomy;

import java.util.List;

import com.github.onsdigital.generator.Folder;

public class T2 extends TaxonomyHome {

	public List<HomeSection> sections;

	public T2(Folder folder, int index) {
		super(folder);
		super.index = index;
		level = "t2";
		lede = "Lorem ipsum dolor sit amet, fugit tation percipitur duo at, magna denique ei duo, mazim oratio volumus id mel. Veri discere accusamus sit id, oblique fabulas legimus sit ex. "
				+ "Atqui urbanitas efficiendi ei sea. Voluptua gloriatur vis an, cu eius semper deleniti vel. Aperiri insolens accommodare vel ne, vel id dictas corrumpit urbanitas.";
		more = "Eam cu alia nostrud. In movet epicuri quo. Decore reprimique vix ei. Ea sit possim albucius, an has mutat lucilius inimicus. Vis et vocibus voluptatum, ne est unum meliore invidunt. "
				+ "Posse nemore vim ei, habeo omnesque ullamcorper usu at, eu eos cetero accusata consulatu.";
	}

}

package com.github.onsdigital.json.bulletin;

import java.util.ArrayList;
import java.util.List;

import com.github.onsdigital.json.Email;
import com.github.onsdigital.json.Section;

public class Bulletin {

	// Top section
	public String title = "Cat Tail Inflation, October 2014";
	public String releaseDate = "19 February 2014";
	public String nextRelease = "21 November 2014";
	public Email contact = new Email();

	// Exec summary
	public String lede = "Shed everywhere meow stretching jump on the table, I don't like that food catnip rip the couch hiss purr sleep in the sink judging you. Bat zzz eat chase the red dot, litter box sunbathe sleep on your face feed me litter box scratched bat scratched. Bat chuf lick chase the red dot eat the grass eat the grass, leap feed me hiss bat run give me fish. Jump lay down in your way lay down in your way give me fish, sunbathe lay down in your way attack your ankles claw attack your ankles leap litter box scratched.";
	public String more = "Purr claw stretching chase the red dot knock over the lamp claw, sleep in the sink zzz lick stuck in a tree. Claw biting scratched biting stretching fluffy fur, fluffy fur lick jump kittens. Catnip puking meow knock over the lamp jump, sleep on your keyboard kittens jump on the table jump sleep on your face run bat. Rip the couch jump biting sniff, sleep on your face chase the red dot leap judging you sleep on your keyboard lick. ";

	// Table of contents
	public List<Section> sections = new ArrayList<Section>();

	/**
	 * Sets up some basic content.
	 */
	public Bulletin() {
		Section summary = new Section();
		summary.title = "Summary";
		summary.markdown = "Cats can haz rule, consectetur adipiscing elit. Suspendisse non tellus nibh. Duis tristique risus sed urna fringilla, eu facilisis orci fringilla. Nullam iaculis libero tempor vehicula ultricies"
				+ "\n\n"
				+ " * The annual rate of output producer price inflation remained low in June, while input prices continued to fall.\n"
				+ " * The output price index for goods produced by UK manufacturers (factory gate prices) rose 0.2% in the year to June, compared with a rise of 0.5% in the year to May.\n"
				+ " * Factory gate prices fell 0.2% between May and June, compared with a fall of 0.1% between April and May\n";
		sections.add(summary);

		Section whatIs = new Section();
		whatIs.title = "What is Producer Price Inflation (PPI)?";

		whatIs.markdown = "It's when dogz strays into territorieses. Etiam fringilla tellus arcu, quis dapibus lacus lacinia a. Vivamus in sollicitudin eros, sed ornare metus. Nam sapien augue, varius bibendum sagittis sed, malesuada quis augue."
				+ "\n\n"
				+ "> This is an explanation box Morbi orci nulla, lobortis pretium auctor non, varius eget mi. Mauris viverra diam quam, at auctor velit placerat sit amet. In sed erat quis elit eleifend tempus. Pellentesque vestibulum orci nec nulla accumsan egestas. Proin mauris ipsum, ornare posuere risus non, consectetur lobortis mi. Pellentesque mi ante, sodales sollicitudin pretium et, varius vel enim."
				+ "\n\n"
				+ "Etiam ac ultricies orci. Pellentesque et posuere tortor. Nunc quam risus, pharetra non condimentum at, pretium at dolor. Maecenas placerat, arcu non consequat venenatis, eros leo eleifend lorem."
				+ "\n\n"
				+ "    sample chart"
				+ "\n\n"
				+ "Proin sed facilisis sapien. Nunc hendrerit dignissim sapien, vel consequat mi rhoncus eget. Maecenas et tellus convallis, tristique risus vitae.";
		sections.add(whatIs);
	}

}

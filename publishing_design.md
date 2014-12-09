# Publishing Design
## Introduction
The Alpha phase has focused on building a prototype ONS website in order to understand and meet the needs of people who use of the information ONS publishes. However, there is a second key group of users whose needs haven't yet received attention: content editors and publishers within ONS.

The question is: "what publishing model best meets the needs of ONS users - and what technical solution best supports this model?" Here I'd like to provide a view of the thinking that has gone into publishing and outline the design that will serve as a starting point for understanding these needs and iterating towards an answer that works.

## Key needs
The initial thinking is based on the following ideas. These provide direction and intention to the design:
 * **9:30 publishing.** This is the most directly measurable indicator of successful operation of ONS as an organisation and a clear opportunity to demonstrate progress.
 * **Consistency and correctness.** Enabling ONS to publish correct statistics and commentary, in a consistent way, allowing us to give the right level of care and attention to the important work that our organisation does.
 * **Disciplined ambition.** Minimising disruption to business as usual and the need for big up-front changes, but at the same time being demanding - in a good way - to keep moving us on. Digital is changing the way government works for the better and getting publishing right is a part of that.
 * **Simplicity.** A design that gets out of the way. The aim is to have minimum distraction and maximum clarity so we can focus on publishing the correct information at the right time. The best possible result is for the publishing tool, like the website, not to be the conversation.
  
## This isn't going to be a CMS
It may be surprising to hear that Tridion is not being considered as a publishing tool. In fact, neither is any off-the-shelf Content Management System. If there's a pre-built product that's right for the job, it makes sense to use it, right? So what's the thinking here?
 * **Too many features.** General purpose content management systems contain a lot of features. That's what makes them general purpose and broadens their appeal. We would only use a fraction of the available functionality - the rest would be clutter.
 * **Not enough features.** ONS has some specific needs when it comes to digital publishing. These aren't well supported by general purpose systems, which means we'll need to customise. That entails ongoing development effort and makes upgrades and operational issue resolution more complex.

Since we have a small set of specific needs and a clearly defined set of pages (we don't need to build freehand web-pages, we're publishing known types of content in a consistent way) - and since we're going to need ongoing development effort whichever way we go - it makes sense to develop a targeted publishing tool.

This is a tradeoff between the effort required to select, customise, operate and upgrade a product that is more complex than necessary and the effort required to build and maintain a small, focused system that directly meets our needs. Both will require ongoing technical skills. Like any tradeoff, the balance can shift, so this might change - particularly if our needs turn out to be bigger than expected.

## The right design
Here are some of the key points of the design:
 * **Familiar tools.** Editors continue, as far as feasible, to prepare statistical content using Word and Excel. There will need to be some changes to templates and layouts, but the aim is to keep things as familiar as far as practical.
 * **Correctness.** Workflow will be limited to a "second pair of eyes" in order to minimise delays, whilst at the same time supporting each other in publishing correct information.
 * **Consistency.** Content will be passed to the Digital Publishing team for copy editing and proofing to ensure consistency across the site. It will be uploaded to a secure preview site using the publishing tool.
 * **Preview.** Editors will be able to view content ready for publishing on the secure preview site and feed back changes. The preview system will be an internal copy of the public website to ensure a true view of how content will appear once published
 * **Publishing.** Because the internal and external versions of the website will be copies of the same system, publishing will consist of directly copying content from the preview site to the public site.

## Technical detail
To round out this overview of the publishing design, here are some specific technical details.
 * **publishing speed.** Two options have been identified to ensure that content can be reliably published between 9:30:00 and 9:30:59. The first relies on improvements in processing power and network speed, combined with a simpler application design: **basic transfer** of content from one system to the other, possibly with optimisation such as gzip encoding. If for any reason this is not a viable approach, a second option can be tested: **encrypted transfer**. This option would involve encrypting content so that it can be safely transferred ahead of time. At 9:30, only an encryption key would need to be transferred in order do decrypt and load the data.
 * **Content as data.** A key enabler of consistent publishing is treating content as data. Simplifying formatting using standards such as Markdown and Json to structure content enables more reliable processing and presentation of content. This approach also provides a signpost to how ONS might provide "data as data" in future - direct machine access to stats.
 * **URL referencing.** Part of the design objective of the Alpha was to demonstrate where possible the value of ensuring that each item of information on the website should exist in a single location and be referenceable by a unique, predictable and readable URL. The aim for Beta is to maintain this approach, enabling additional items such as charts to be referenced and embedded, whether in statistical bulletins, or in articles publisheb by third-party websites.
 * **Page types.** Because the new website will have clear page types, the publishing tool can be developed to specifically allow population of relevant data for those pages. This means there's no need to have functionality to build web page templates. Providing users are able to enter (or select via URL reference) the correct data, there should be no need for a full-featured web CMS approach. For content which requires more free input, such as statistical bulletins, articles and methodology, the aim is to make use of a combination of Markdown and the [Sir Trevor](http://madebymany.github.io/sir-trevor-js/) approach to constraining content.


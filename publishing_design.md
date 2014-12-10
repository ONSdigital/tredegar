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
To round out this overview of the publishing design, here are some specific technical details. For a picture of how this fits together, feel free to take a look at [this diagram](https://docs.google.com/drawings/d/1Q9GQNx3-73KeCThtPYBboi7eFcn4VylHMlBfu9yYhfk/edit?usp=sharing).
 * **publishing speed.** Two options have been identified to ensure that content can be reliably published between 9:30:00 and 9:30:59. The first relies on improvements in processing power and network speed, combined with a simpler application design: **basic transfer** of content from one system to the other, possibly with optimisation such as gzip encoding. If for any reason this is not a viable approach, a second option can be tested: **encrypted transfer**. This option would involve encrypting content so that it can be safely transferred ahead of time. At 9:30, only an encryption key would need to be transferred in order do decrypt and load the data.
 * **Content as data.** A key enabler of consistent publishing is treating content as data. Simplifying formatting using standards such as Markdown and Json to structure content enables more reliable processing and presentation of content. This approach also provides a signpost to how ONS might provide "data as data" in future - direct machine access to stats.
 * **URL referencing.** Part of the design objective of the Alpha was to demonstrate where possible the value of ensuring that each item of information on the website should exist in a single location and be referenceable by a unique, predictable and readable URL. The aim for Beta is to maintain this approach, enabling additional items such as charts to be referenced and embedded, whether in statistical bulletins, or in articles publisheb by third-party websites.
 * **Page types.** Because the new website will have clear page types, the publishing tool can be developed to specifically allow population of relevant data for those pages. This means there's no need to have functionality to build web page templates. Providing users are able to enter (or select via URL reference) the correct data, there should be no need for a full-featured web CMS approach. For content which requires more free input, such as statistical bulletins, articles and methodology, the aim is to make use of a combination of Markdown and the [Sir Trevor](http://madebymany.github.io/sir-trevor-js/) approach to constraining content.

### 9:30 publishing
Some of the largest releases are up to 150M. Given modern infrastructure and network connectivity, transferring this volume of data should not be an issue. The bottleneck with the current publishing solution is not so much bandwidth as the amount and complexity of processing needed once the data arrive.

This suggests that directly transferring Json documents from a MongoDB database for the secure instance to a MongoDB instance for the public site should be feasible, because the processing requirement to complete publishing is substantially less than for the current solution. Where there is a need to transfer files as well, although the data size is likely to be larger, network speed should mean this is also not an issue. 

If an encryption solution is required, this would add some complexity, but has the benefit that data can be transferred and staged well ahead of publishing time, so ensuring that everything is in place, but unreadable, on the public server. At 9:30, decryption key(s) would be transferred to the server in order to decrypt the data (it would be relatively simple to generate a unique, random encryption key for every published item, reducing the impact of any compromise).

In terms of security, encrypted data can be safely transmitted in the clear (as with HTTPS over the Web) so storing encrypted data on the public server adds no additional security burden to the solution (it's considered IL0 in traditional parlance). AES encryption is fast (as compared to, say RSA) and it should therefore be no problem to decrypt and load even large volumes of data within the publishing window. On a multi-core server, this can be carried out concurrently, enabling parallel decryption of multiple items.

### Content as data
With Json established as a defacto standard for representing structured information and Markdown in widespread use by non-technical bloggers, the opportunity to store document content as data with simple, declarative formatting becomes a natural choice.

The content for each page can be represented as a Json file that describes title, any URL-referenced items such as statistical bulletins, timeseries and datasets as well as blocks of Markdown for pages such as Articles, Methodology and Bulletins.

This makes page content natural to store in Mongo and work with in Javascript (and other languages). It also provides the option of building a structured content editing interface to view and update the values stored in the Json. The great thing is that there's no need to store HTML, or work with templates in the database - the front-end can focus on presentation and the back-end can focus on data.

### URL Referencing
One of the key foundations of the Web is URL addressability of resources. The Alpha was built as a single-page web application for expediency of development, but it's clear that fundamentally the website isn't actually a single-page web application, it's actually a collection of addressable resources, including timeseries pages, charts, bulletins, etc.

The Alpha made a point of ensuring that everythng has a unique location and can be cross-referenced within the site using links. For example, some timeseries will appear on more than one T3 "Product" page and, if you click through, you will see the breadcrumb change to reflect the actual location of that timeseries within the taxonomy.

Although the front end uses /#!/ URLs, the data behind each page is addressable. If you edit the url, substituting /data/ instead, you can view the Json that provides the content of any given page.

The aim for Beta will be to build on this by enabling additional items, such as individual charts, to be addressable. This would enable, for example, news websites to directly embed a chart, at a point in time, into an article.

### Page types
The Alpha has shown that, for what ONS needs to achieve, having a defined set of page types provides clarity and structure. This means that a dozen or so templates are sufficient, so providing ONS content publishers with a user-friendly way to edit Json values and Markdown content should be sufficient to support the functionality needed on the site.

It should therefore be simpler to develop a straightforward publishing tool to provide simple forms that correspond to the Json data structures than to customise a full-featured CMS to provide this functionality.


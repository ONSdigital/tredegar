// Read a page's GET URL variables and return them as an associative array.  
// Refer to: http://jquery-howto.blogspot.co.uk/2009/09/get-url-parameters-values-with-jquery.html
// Modified by Bren: Exclude anchor at the end of url
$.extend({
  getUrlVars: function(){
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
    	if ((i + 1) === hashes.length) { // last parameter
				anchorIndex = hashes[i].indexOf("#")
				if (anchorIndex > -1) {
					hashes[i] = hashes[i].substr(0,anchorIndex);				
				}
    		}
      hash = hashes[i].split('=');
      vars.push(hash[0]);
      if(hash[1]) {
        hash[1] =  hash[1].replace(/\+/g, ' ');
      }
      vars[hash[0]] = hash[1];
    }
    return vars;
  },
  getUrlVar: function(name){
    return $.getUrlVars()[name];
  },
	getAnchor: function(){
		var anchorIndex = window.location.href.indexOf('#');
		var anchor;	
		if (anchorIndex > -1) {
   		anchor = window.location.href.slice( anchorIndex + 1);
 		}
    return anchor;
  },
});
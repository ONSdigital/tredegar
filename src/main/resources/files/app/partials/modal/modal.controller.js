(function() {

	'use strict';

	angular.module('onsTemplates')
		.controller('ModalController', ['ipCookie' , 'PageUtil',ModalController] )


	function ModalController(ipCookie, PageUtil) {
		var modal = this
		modal.showModal = isAccepted() ? false : true
		modal.showModal = PageUtil.isPrerender() ? false : modal.showModal

		function isAccepted() {
			var accepted = ipCookie('onsAlphaDisclaimer')
			return accepted

		}

		function acceptDisclaimer() {
			ipCookie('onsAlphaDisclaimer', true)
			modal.showModal = false
		}

		angular.extend(modal, {
			acceptDisclaimer:acceptDisclaimer
		})
	}


})()
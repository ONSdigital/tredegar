(function() {

	'use strict';

	angular.module('onsTemplates')
		.controller('ModalController', ['ipCookie',ModalController] )


	function ModalController(ipCookie) {
		var modal = this
		modal.showModal = isAccepted() ? false : true

		function isAccepted() {
			var accepted = ipCookie('onsAlphaDisclaimer')
			console.log(accepted)
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
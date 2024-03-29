/* ===========================================================
  # bootstrap-confirmation v1.0.3

  This is a fork of ethaizone's [original code](https://github.com/ethaizone/Bootstrap-Confirmation)

  with some help from [jibe914](https://github.com/jibe914/Bootstrap-Confirmation)
  and [MisatoTremor](https://github.com/MisatoTremor/bootstrap-confirmation)

  Confirmation plugin compatible with Twitter Bootstrap 3 extending Popover

  ## Usage

  Create your `button or link` with the `data-toggle="confirmation"`.

      <a href="http://google.com" class="btn" data-toggle="confirmation">Confirmation</a>

  Enable plugin via JavaScript:

      $('[data-toggle="confirmation"]').confirmation();

  Or if you want to assing to one element

      <a href="http://google.com" class="btn confirmation">Confirmation</a>
      $('.confirmation').confirmation();

  ## Options

  In addition to the standard bootstrap popover options, you now have access to the following options

  + **btnOkClass**
  Set the Ok button class. `default: btn btn-sm btn-danger`

  + **btnOkLabel**
  Set the Ok button label. `default: Delete`

  + **btnOkIcon**
  Set the Ok button icon. `default: glyphicon glyphicon-ok`

  + **btnCancelClass**
  Set the Ok button class. `default: btn btn-sm btn-default`

  + **btnCancelLabel**
  Set the Ok button label. `default: Cancel`

  + **btnCancelIcon**
  Set the Ok button icon. `default: glyphicon glyphicon-remove`

  + **singleton**
  Set true to allow only one confirmation to show at a time. `default: true`

  + **popout**
  Set true to hide the confirmation when user clicks outside of it. `default: true`

  + **onShow**
  Callback when popup show. `default: function(event, element){}`

  + **onHide**
  Callback when popup hide. `default: function(event, element){}`

  + **onConfirm**
  Callback for confirm button. `default: function(event, element){}`

  + **onCancel**
  Callback for cancel button. `default: function(event, element){}`

  ## Copyright and license

  Copyright (C) 2013 bootstrap-confirmation

  Licensed under the MIT license.

  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 * =========================================================== */

+function ($) {
	'use strict';

	//var for check event at body can have only one.
	var event_body = false;

	// CONFIRMATION PUBLIC CLASS DEFINITION
	// ===============================
	var Confirmation = function (element, options) {
		var that = this;

		this.init('confirmation', element, options);

		$(element).on('show.bs.confirmation', function(e) {
			that.options.onShow(e, this);

			$(this).addClass('open');

			var options = that.options;
			var all = options.all_selector;

			if(options.singleton)
			{
				$(all).not(that.$element).each(function()
				{
					if( $(this).hasClass('open') )
					{
						$(this).confirmation('hide');
					}
				});
			}
		});

		$(element).on('hide.bs.confirmation', function(e) {
			that.options.onHide(e, this);

			$(this).removeClass('open');
		});

		$(element).on('shown.bs.confirmation', function(e) {
			var options = that.options;
			var all = options.all_selector;

			if(that.isPopout()) {
				if(!event_body) {
					event_body = $('body').on('click', function (e) {
						if(that.$element.is(e.target)) return;
						if(that.$element.has(e.target).length) return;
						if($('.popover').has(e.target).length) return;

						that.hide();
						that.inState.click = false;

						$('body').unbind(e);

						event_body = false;

						return;
					});
				}
			}
		});

		$(element).on('click', function(e) {
			e.preventDefault();
		});
	}

	if (!$.fn.popover || !$.fn.tooltip) throw new Error('Confirmation requires popover.js and tooltip.js');

	Confirmation.VERSION  = '1.0.2'

	Confirmation.DEFAULTS = $.extend({}, $.fn.popover.Constructor.DEFAULTS, {
		placement 		: 'right',
		title 			: 'Are you sure?',
		btnOkClass 		: 'btn btn-sm btn-danger',
		btnOkLabel 		: 'Delete',
		btnOkIcon 		: 'glyphicon glyphicon-ok',
		btnCancelClass 	: 'btn btn-sm btn-default',
		btnCancelLabel 	: 'Cancel',
		btnCancelIcon 	: 'glyphicon glyphicon-remove',
		href 			: '#',
		target 			: '_self',
		singleton 		: true,
		popout 			: true,
		onShow 			: function(event, element){},
		onHide 			: function(event, element){},
		onConfirm 		: function(event, element){},
		onCancel 		: function(event, element){},
		template 		:   '<div class="popover"><div class="arrow"></div>'
							+ '<h3 class="popover-title"></h3>'
							+ '<div class="popover-content">'
							+ '<a data-apply="confirmation">Yes</a>'
							+ '<a data-dismiss="confirmation">No</a>'
							+ '</div>'
							+ '</div>'
	});


	// NOTE: CONFIRMATION EXTENDS popover.js
	// ================================
	Confirmation.prototype = $.extend({}, $.fn.popover.Constructor.prototype);

	Confirmation.prototype.constructor = Confirmation;

	Confirmation.prototype.getDefaults = function () {
		return Confirmation.DEFAULTS;
	}

	Confirmation.prototype.setContent = function () {
		var that       = this;
		var $tip       = this.tip();
		var title      = this.getTitle();
		var $btnOk     = $tip.find('[data-apply="confirmation"]');
		var $btnCancel = $tip.find('[data-dismiss="confirmation"]');
		var options    = this.options

		$btnOk.addClass(this.getBtnOkClass())
			.html(this.getBtnOkLabel())
			.prepend($('<i></i>').addClass(this.getBtnOkIcon()), " ")
			.attr('href', this.getHref())
			.attr('target', this.getTarget())
			.off('click').on('click', function(event) {
				options.onConfirm(event, that.$element);

				// If the button is a submit one
				if (that.$element.attr('type') == 'submit')
					that.$element.closest('form:first').submit();

				that.hide();
				that.inState.click = false;
			});

		$btnCancel.addClass(this.getBtnCancelClass())
			.html(this.getBtnCancelLabel())
			.prepend($('<i></i>').addClass(this.getBtnCancelIcon()), " ")
			.off('click').on('click', function(event){
				options.onCancel(event, that.$element);

				that.hide();
				that.inState.click = false;
			});

		$tip.find('.popover-title')[this.options.html ? 'html' : 'text'](title);

		$tip.removeClass('fade top bottom left right in');

		// IE8 doesn't accept hiding via the `:empty` pseudo selector, we have to do
		// this manually by checking the contents.
		if (!$tip.find('.popover-title').html()) $tip.find('.popover-title').hide();
	}

	Confirmation.prototype.getBtnOkClass = function () {
		var $e = this.$element;
		var o  = this.options;

		return $e.attr('data-btnOkClass') || (typeof o.btnOkClass == 'function' ? o.btnOkClass.call($e[0]) : o.btnOkClass);
	}

	Confirmation.prototype.getBtnOkLabel = function () {
		var $e = this.$element;
		var o  = this.options;

		return $e.attr('data-btnOkLabel') || (typeof o.btnOkLabel == 'function' ? o.btnOkLabel.call($e[0]) : o.btnOkLabel);
	}

	Confirmation.prototype.getBtnOkIcon = function () {
		var $e = this.$element;
		var o  = this.options;

		return $e.attr('data-btnOkIcon') || (typeof o.btnOkIcon == 'function' ?  o.btnOkIcon.call($e[0]) : o.btnOkIcon);
	}

	Confirmation.prototype.getBtnCancelClass = function () {
		var $e = this.$element;
		var o  = this.options;

		return $e.attr('data-btnCancelClass') || (typeof o.btnCancelClass == 'function' ? o.btnCancelClass.call($e[0]) : o.btnCancelClass);
	}

	Confirmation.prototype.getBtnCancelLabel = function () {
		var $e = this.$element;
		var o  = this.options;

		return $e.attr('data-btnCancelLabel') || (typeof o.btnCancelLabel == 'function' ? o.btnCancelLabel.call($e[0]) : o.btnCancelLabel);
	}

	Confirmation.prototype.getBtnCancelIcon = function () {
		var $e = this.$element;
		var o  = this.options;

		return $e.attr('data-btnCancelIcon') || (typeof o.btnCancelIcon == 'function' ? o.btnCancelIcon.call($e[0]) : o.btnCancelIcon);
	}

	Confirmation.prototype.getHref = function () {
		var $e = this.$element;
		var o  = this.options;

		return $e.attr('data-href') || (typeof o.href == 'function' ? o.href.call($e[0]) : o.href);
	}

	Confirmation.prototype.getTarget = function () {
		var $e = this.$element;
		var o  = this.options;

		return $e.attr('data-target') || (typeof o.target == 'function' ? o.target.call($e[0]) : o.target);
	}

	Confirmation.prototype.isPopout = function () {
		var popout;
		var $e = this.$element;
		var o  = this.options;

		popout = $e.attr('data-popout') || (typeof o.popout == 'function' ? o.popout.call($e[0]) :	o.popout);

		if(popout == 'false') popout = false;

		return popout
	}


	// CONFIRMATION PLUGIN DEFINITION
	// =========================
	var old = $.fn.confirmation;

	$.fn.confirmation = function (option) {
		var that = this;

		return this.each(function () {
			var $this            = $(this);
			var data             = $this.data('bs.confirmation');
			var options          = typeof option == 'object' && option;

			options              = options || {};
			options.all_selector = that.selector;

			if (!data && option == 'destroy') return;
			if (!data) $this.data('bs.confirmation', (data = new Confirmation(this, options)));
			if (typeof option == 'string') data[option]();
		});
	}

	$.fn.confirmation.Constructor = Confirmation


	// CONFIRMATION NO CONFLICT
	// ===================
	$.fn.confirmation.noConflict = function () {
		$.fn.confirmation = old;

		return this;
	}
}(jQuery);

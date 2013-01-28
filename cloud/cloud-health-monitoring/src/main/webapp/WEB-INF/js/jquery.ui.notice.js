/*
 * jQuery UI Notice Plugin 0.3
 *
 * Copyright 2009-2011, Igor 'idle sign' Starikov
 *
 * https://github.com/idlesign/ist-ui-notice
 *
 * Depends:
 *	jquery.ui.core.js
 *	jquery.ui.widget.js
 *  jquery.effects.pulsate.js
 */
(function($, undefined) {

    $.widget('ui.notice', {

        options: {
            type: 'info',
            content: false,
            pulsate: false,
            visible: true,
            autoHideTimeout: false,
            showIcon: true,
            iconInfo: 'ui-icon-info',
            iconAlert: 'ui-icon-alert'
        },

        _create: function() {
            var o = this.options, self = this;

            var noticeBox = this.element;
            if (!o.visible) {
                noticeBox.hide();
            }
            noticeBox.attr('role', 'notice');
            noticeBox.addClass('ui-widget');
            noticeBox.wrapInner('<div><p></p></div>');


            var state = 'highlight';
            var icon = o.iconInfo;
            if (o.type == 'alert') {
                state = 'error';
                icon = o.iconAlert;
            }
            var innerBox = noticeBox.children(':eq(0)').addClass('ui-corner-all ui-notice-inner ui-state-' + state);
            var contentBox = innerBox.children(':eq(0)');
            var content = contentBox.html();

            if (o.content) {
                content(o.content);
            }

            if (o.showIcon) {
                contentBox.prepend('<span/>');
                var iconBox = contentBox.children(':eq(0)');
                iconBox.addClass('ui-icon ' + icon).css({'float': 'left', 'margin-right': '0.3em'});
            }

            if (o.visible && o.pulsate) {
                this.animate();
            }

            if (o.autoHideTimeout) {
                setTimeout(function(self){ self.element.hide(); }, o.autoHideTimeout, self)
            }
        },

        animate: function() {
            this.element.effect('pulsate', { times:3 }, 1000);
        },

        destroy: function() {
            this.element.find('.ui-icon').eq(0).remove();
            var content = this.element.children(':eq(0)').children(':eq(0)').html();
            this.element.removeAttr('role').removeClass('ui-widget').html(content);
        }

    });

    $.extend($.ui.notice, {
        version: "0.3"
    });

})(jQuery);
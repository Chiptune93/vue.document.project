
Vue.mixin({
	data: function () {
		return {
		};
	},
	created: function () {
	},
	methods: {
		_openPopup: function (opts) {
			var dialog = this.$dialog.open(opts);
			return dialog;
		},
		_closePopup: function (result) {
			if (typeof result === 'function') this.$parent._closePopup = result;
			else this.callback(result);
		},
		_alert: function (opts, callback) {
			var dialog = this.$dialog.alert(opts, callback);
			return dialog;
		},
		_confirm: function (opts, callbackOk, callbackCancel) {
			var dialog = this.$dialog.confirm(opts, callbackOk, callbackCancel);
			return dialog;
		},
		_close: function (dialog) {
			this.$dialog.close(dialog);
		},
	}
});


Vue.component("popup-content", {
	name: 'popup-content',
	created() {
		popupContentVm = this;
	},
	render(createElement) {
		var children = popupContentList.map(popup => createElement(
			popup.component,
			{
				key: popup.key,
				props: popup.params.params ? popup.params.params : {},
				hook: {
					create: function (_, vnode) {
						popup.componentInstance = vnode.componentInstance;
					}
				}
			}
		));

		return createElement('div', children);
	}
});

var nextContentKey = 0;
var popupContentVm = null;
var popupContentList = [];
var popupTemplate = {
	template: `<div class="modal_wrap">
        <div class="overlay" :style="{zIndex:zIndex-1}" @click="close();"></div>
        <div :class="popupClass" :style="{zIndex:zIndex}">
			<div class="modal_head">
				<h3 class="tit">{{params.title||title}}</h3>
				<a href="javascript:void(0)" class="close" @click="close()">닫기</a>	
			</div>
			<div :class="contClassObj">			
				<div :class="contInnerClass">
					<popup-content v-bind:popupcontentkey="popupcontentkey" v-bind:callback="params.callback" :ref="popupcontentkey" @update:popup="updatePopup"></popup-content>
				</div>
			</div>
			<div class="modal_btn" v-show="!hideBtn">
				<div v-if="false"><input type="checkbox" onclick="">다음부터 표시하지 않기</div>
				<div class="btn_wrap" v-if="btns.length == 0">
					<a href="javascript:void(0)" class="btn col07" v-if="showClose" @click="close()">{{nameClose}}</a>
					<a href="javascript:void(0)" class="btn" v-if="showOk" @click="ok()">{{nameOk}}</a>
				</div>
				<div class="btn_wrap" v-if="btns.length > 0">
					<a href="javascript:void(0)" :class="btn.class" :data-lang-flag="btn.flag" @click="btn.click(v)" v-for="btn in btns">{{btn.name}}</a>
				</div>
	        </div>
		</div>
    </div>`,
	props: ['dialogkey', 'params'],
	data: function () {
		return {
			showOk: true,
			nameOk: '확인',
			showClose: true,
			nameClose: '취소',
			hideBtn: false,
			title: '팝업',
			zIndex: 8999,
			callback: function () { },
			result: {},
			popupcontentkey: '',
			popupClass: { modal_box: true },
			contInnerClass: { inner: true },
			isFull: true,
			isBottom: false,
			btns: [],
			v: null,
			isOverVisible: false
		};
	},
	created: function () {
		popupContentList = [];

		this.hideBtn = util.isNotEmpty(this.params.hideBtn);
		this.v = this;
		if (this.params.btns) {
			this.btns = this.params.btns;
		}
		if (this.params.type == 'bottom') {
			this.popupClass["m_bottom"] = true;
		}
		if (this.params.full) {
			this.popupClass["m_full"] = true;
		}
		this.showClose = util.getBool(this.params.showClose);
		this.showOk = util.getBool(util.null2Str(this.params.showOk, 'true'));
		if (this.params.size) {
			this.popupClass["wid" + this.params.size] = true;
		}

		if (this.params.contInnerSize) this.contInnerClass["mwid" + this.params.contInnerSize] = true;

		var key = "_popup_cont_key_" + (nextContentKey++);
		this.zIndex += nextContentKey;
		this.popupcontentkey = key;
		var component;
		if (this.params.path) {
			var popupContents = httpVueLoader(this.params.path + "?ver=" + comm.version);
			component = popupContents;
		} else if (this.params.template) {
			component = this.params.template;
		}

		var vm = this;
		vm.callback = this.params.callback;
		this.params.callback = function (result) {
			vm.ok(result);
		}
		this.params.popupcontentkey = this.popupcontentkey;
		popupContentList.push({ component: component, key: key, params: { params: this.params } });

		if (anchorVm) anchorVm.$forceUpdate();
	},
	mounted: function () {
		this.nameOk = util.null2Str(this.params.nameOk, comm.getMessage('C10020005'));
		this.nameClose = util.null2Str(this.params.nameClose, comm.getMessage('C10020006'));
	},
	computed: {
		contClassObj: function () {
			return {
				modal_cont: true,
				t_left: true,
				over_visible: this.isOverVisible
			};
		}
	},
	methods: {
		ok: function (result) {
			comm.polling = false;
			var result = result || this.$refs[this.popupcontentkey]._closePopup();
			if (result instanceof Promise) {
				var vm = this;
				result.then(function (result) {
					var r = true;
					if (typeof vm.callback === 'function') r = vm.callback(result);
					if (r != false) vm.$dialog.close(vm);
				}).catch(function (errMsg) {
					vm._alert(errMsg || "");
					return;
				});
			} else {
				var r = true;
				if (typeof this.callback === 'function') r = this.callback(result);
				if (r != false) this.$dialog.close(this);
			}
		},
		close: function () {
			comm.polling = false;
			if (typeof this.callback === 'function') this.callback({});
			this.$dialog.close(this);
		},
		updatePopup: function (popup) {
			if (popup) {
				this.isOverVisible = popup.isOverVisible;
			}
		}
	}
};

var dialogTemplate = {
	template: `<div class="modal_wrap">
        <div class="overlay" :style="{zIndex:zIndex-1}"></div>
        <div class="modal_box" :style="{zIndex:zIndex}">
			<div class="modal_head" style="display:none;">
				<h3 class="tit" :data-lang-flag="langFlagTitle" >{{title}}</h3>
				<a href="javascript:void(0)" class="close" @click="close()">닫기</a>	
			</div>
			<div class="modal_cont">
				<div class="inner">
					<div :class="iconClass">
						<p v-html="getMsg()"></p>
					</div>
				</div>
			</div>
			<div class="modal_btn">
				<div class="btn_wrap" v-if="buttonCount == 1">
					<a href="javascript:void(0)" class="btn" data-lang-flag="C10020005" ref="btnOk" v-if="showOk" @click="ok()">확인</a>
				</div>
				<div class="btn_wrap" v-if="buttonCount == 2">
					<a href="javascript:void(0)" class="btn col07" data-lang-flag="C10020006" v-if="showClose" @click="close()">취소</a>
					<a href="javascript:void(0)" class="btn col08" data-lang-flag="C10020005" ref="btnOk" v-if="showOk" @click="ok()">확인</a>
				</div>
	        </div>
		</div>
    </div>`,
	props: ['dialogkey', 'button', 'msg', 'callbackOk', 'type', 'opts'],
	data: function () {
		return {
			showOk: true,
			showClose: false,
			title: '알림',
			langFlagTitle: 'C10010001',
			isMessageChild: false,
			zIndex: 9999,
			buttonCount: 2,
			iconClass: 'alert_box',
			timeoutVal: null
		};
	},
	created: function () {
		if (this.opts) {
			this.isMessageChild = this.opts.isMessageChild;
		}

		if (this.button && this.button.type == 'one') this.buttonCount = 1;

		if (this.type == 'notice') this.buttonCount = 1;
		else this.iconClass = 'alert_box confirm';

		if (this.type == 'confirm') {
			this.showClose = true;
			this.langFlagTitle = 'C10010002';
		}

		if (this.type == 'notice') {
			//this.timeoutVal = setTimeout(this.ok,2000);
		}
	},
	mounted: function () {
		this.$refs.btnOk.focus();
	},
	methods: {
		getMsg: function () {
			return this.msg.replace(/(\r\n\t|\n|\r\t)/gm, "<br/>");
		},
		ok() {
			if (typeof this.callbackOk === 'function') this.callbackOk('ok');
			else if (this.opts && typeof this.opts.callbackOk === 'function') this.opts.callbackOk('ok');

			if (typeof this.opts.focus === 'object') {
				this.opts.focus.focus();
			}

			if (!this.isMessageChild) this.$dialog.close(this);
		},
		close() {
			clearTimeout(this.timeoutVal);
			if (typeof this.callbackCancel === 'function') this.callbackCancel('cancel');
			else if (this.opts && typeof this.opts.callbackCancel === 'function') this.opts.callbackCancel('ok');
			this.$dialog.close(this);
		}
	}
};

var nextKey = 0;
var anchorVm = null;
var popupList = [];

var dialog = {
	open: function (params) {
		var component = popupTemplate;

		var key = "_popup_key_" + (nextKey++);
		params.dialogkey = key;
		popupList.push({ component: component, key: key, params: { params } });

		if (anchorVm)
			anchorVm.$forceUpdate();

		return { popup_key: key };
	},
	alert: function (opts, callback) {
		var params = opts;
		if (typeof opts === 'string') params = { msg: opts };

		if (typeof callback === 'function') params.callbackOk = callback;
		else if (typeof callback === 'object') params.callbackOk = function () { callback.focus(); };
		if (typeof opts.callback === 'function') params.callbackOk = opts.callback;

		var component = dialogTemplate;

		var key = "_popup_key_" + (nextKey++);
		params.dialogkey = key;
		params.type = "notice";
		params.opts = opts;
		popupList.push({ component: component, key: key, params: params });

		if (anchorVm)
			anchorVm.$forceUpdate();

		return { popup_key: key };
	},
	confirm: function (opts, callbackOk, callbackCancel) {
		var params = opts;
		if (typeof opts === 'string') params = { msg: opts };
		if (typeof callbackOk === 'function') params.callbackOk = callbackOk;
		if (typeof callbackCancel === 'function') params.callbackCancel = callbackCancel;
		if (typeof opts.callback === 'function') params.callbackOk = opts.callback;

		var component = dialogTemplate;

		var key = "_popup_key_" + (nextKey++);
		params.dialogkey = key;
		params.type = "confirm";
		params.opts = opts;
		popupList.push({ component: component, key: key, params: params });

		if (anchorVm)
			anchorVm.$forceUpdate();

		return { popup_key: key };
	},
	close: function (inst) {
		if (!inst)
			return;

		for (var i = 0; i < popupList.length; i++) {
			if (inst instanceof Vue && inst === popupList[i].componentInstance ||
				inst.popup_key === popupList[i].key) {
				popupList.splice(i, 1);
				if (anchorVm)
					anchorVm.$forceUpdate();
				return;
			}
		}
	}
};

Vue.component("popup-anchor", {
	name: 'popup-anchor',
	created() {
		anchorVm = this;
	},
	render(createElement) {
		var children = popupList.map(popup => createElement(
			popup.component, {
			key: popup.key,
			props: popup.params ? popup.params : {},
			hook: {
				create: function (_, vnode) {
					popup.componentInstance = vnode.componentInstance;
				}
			}
		}
		));

		return createElement('div', children);
	}
});

Object.defineProperty(Vue.prototype, '$dialog', {
	get() { return dialog }
});

Object.defineProperty(Vue.prototype, '$comm', {
	get() { return comm }
});

Object.defineProperty(Vue.prototype, '$util', {
	get() { return util }
});
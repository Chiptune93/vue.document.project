var includeJs = function (jsPath) {
	var js = document.createElement("script");
	js.defer = true;
	js.type = "text/javascript";
	js.src = jsPath;

	document.head.appendChild(js);
}

var includeCss = function (cssPath) {
	var css = document.createElement("link");
	css.type = "text/css";
	css.rel = "stylesheet";
	css.href = cssPath;
	css.media = "all";

	document.head.appendChild(css);
}

var request = function (path) {
	return new Promise(function (resolve, reject) {
		var xhr = new XMLHttpRequest();

		xhr.open('GET', path, true);
		xhr.withCredentials = true;
		xhr.onreadystatechange = function () {
			if (xhr.readyState == 4 && xhr.status == 200) {
				resolve(xhr.responseText);
			}
		};
		xhr.send(null);
	})
}


let Store;
window.addEventListener('load', function () {
	document.title = "Documents";

	var pa = document.createElement("popup-anchor");
	var ct = document.getElementById("app");
	ct.append(pa);


	documentReady({});
});

var comm = {
	/* 캐시가 안남도록 버전 정의하여 파일 임포트 시 사용 */
	version: Number(new Date()),
	/* ajax call setting  :: 아직 전체 미정의 상태. 아래의 코드들은 ACaaS 에서 가져온 것이므로 삭제 해도 무방. */
	ajax: function (opt, callFc) {
		var opt = util.null2Str(opt, {});
		var url = opt.url;
		var params = opt.params || {};
		var method = opt.method || "get";
		var loading = opt.loading != false;
		var _callFc = callFc;

		if (typeof opt == 'function') _callFc = opt;

		/* 요청 성공시 실행 */
		var succesFc = function (response) {
			try {
				var result = {
					"result": response.data
				};
				comm.loading(false);
				_callFc(result);
			} catch (e) { }
		};

		/* 요청 catch 시 실행 */
		var errorFc = function (error) {
			comm.loading(false);
			if (error.response) {
				if (!anchorVm) {
					anchorVm = new Vue();
				}
			}
		};

		/* 요청 이후 마지막에 실행 되는 함수 */
		var finallyFc = function () {

		};
		var port = "8080";


		axios.defaults.baseURL = "http://" + location.host + "/";
		if (location.host == "localhost") axios.defaults.baseURL = axios.defaults.baseURL = "http://localhost:" + port + "/";
		axios.defaults.withCredentials = true;
		//
		axios.defaults.headers.common["pathname"] = location.pathname;
		axios.defaults.headers.common["loading"] = loading;

		//요청 인터셉터 
		axios.interceptors.request.use(
			function (config) {
				if (config.headers.common["loading"]) comm.loading(true);
				return config
			},
			function (error) {
				if (loading) comm.loading(false);
				return Promise.reject(error);
			}
		);

		axios.interceptors.response.use(
			function (response) {
				let arr = response.config.baseURL.split("/");
				if (loading) comm.loading(false);
				return response;
			},
			function (error) {
				if (loading) comm.loading(false);
				// 응답 에러 시에도 로딩 끄기
				return Promise.reject(error);
			}
		);

		if (method == "post")
			axios.post(url, params).then(succesFc).catch(errorFc).finally(finallyFc);
		else if (method == "put")
			axios.put(url, params).then(succesFc).catch(errorFc).finally(finallyFc);
		else if (method == "delete")
			axios.delete(url, { data: params }).then(succesFc).catch(errorFc).finally(finallyFc);
		else
			axios.get(url, params).then(succesFc).catch(errorFc).finally(finallyFc);

	},

	get: function (opt, callFc) {
		opt.method = "get";
		this.ajax(opt, callFc);
	},
	post: function (opt, callFc) {
		opt.method = "post";
		this.ajax(opt, callFc);
	},
	put: function (opt, callFc) {
		opt.method = "put";
		this.ajax(opt, callFc);
	},
	delete: function (opt, callFc) {
		opt.method = "delete";
		this.ajax(opt, callFc);
	},
	/* 로딩 표시 띄우는 용도 */
	loading: function (isOn) {
		try {
			document.getElementsByClassName('loading_box_wrap')[0].className = 'loading_box_wrap ' + (isOn ? 'on' : '');
		} catch (e) { }
	},

}
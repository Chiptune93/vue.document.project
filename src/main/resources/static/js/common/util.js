var util = {
    null2Str: function(obj, defaultValue) {
        if (obj == null || obj == undefined || obj == 'undefined' || obj === '') {
            return defaultValue || "";
        }
        return obj;
    },
    isEmpty : function(obj){
        if (obj == null || obj == undefined || obj == 'undefined' || obj === '') return true;
        
    	if(obj instanceof Date) return false;
    	if(obj instanceof File) return false;
    	if(typeof obj === 'array') return obj.length == 0;
    	if(typeof obj === 'object') return Object.keys(obj).length == 0;
		return util.null2Str(obj) === "";
	},
    isNotEmpty : function(obj){
		return !util.isEmpty(obj);
	},
    isObject : function(obj){
		return obj.constructor == Object;
	},
	null2Num : function(val,defaultValue){
		if(!isNaN(val)) return Number(val);
		return defaultValue || 0;
	},
	getBool : function(val){
		return (val == true || val == 'true');
	},
    getQueryStringObject: function() {
        var a = window.location.search.substr(1).split('&');
        if (a == "") return {};
        var b = {};
        for (var i = 0; i < a.length; ++i) {
            var p = a[i].split('=', 2);
            if (p.length == 1)
                b[p[0]] = "";
            else
                b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
        }
        return b;
    },
    format:function(valStr,tp){
        var regExp;
        if (tp == "EMAIL") {
            regExp = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return valStr.match(regExp);
        } else if (tp == "NUMBER") {
            return util.formatNumber(valStr);
        } else if (tp == "HP") {
            regExp = /(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})/g;
            return valStr.match(regExp);
        } else if (tp == "KA") {
            regExp = /^[ㄱ-ㅎ|가-힣|a-z|A-Z|\*]+$/;
            return valStr.match(regExp);
        } else if (tp == 'DATE') {
			return util.formatConvert(valStr,'yyyy.MM.dd');
		}
        return valStr;
		
	},
    checkFormat: function(valStr, tp) {
		if(util.isEmpty(valStr)) return false;
		
        if (tp == "EMAIL") {
            return (util.format(valStr,tp) != null);
        } else if (tp == "NUMBER") {
            return !isNaN(valStr);
        } else if (tp == "HP") {
			var regExp = /^01([0|1|6|7|8|9]?)-?([0-9]{3,4})-?([0-9]{4})$/;
            return regExp.test(valStr) && valStr.length >= 10;
        } else if (tp == "KA") {
            return (util.format(valStr,tp) != null)
        }
        return false;
    },
    checkPwd: function(pwd) {
        var p1 = /[0-9]/;
        var p2 = /[a-zA-Z]/;
        var p3 = /[~!@\#$%<>^&*]/;
        return (p1.test(pwd) && p2.test(pwd) && p3.test(pwd) && pwd.length <= 12 && pwd.length >= 6);
    },
    formatConvert: function(val, type) {
        if (!val) return "";
        
        var jsDate = null;
        var dateParts = val.replace(/[^0-9]/g, "");
        if (dateParts.length < 1) return val;
        if (dateParts.length == 4) {
            jsDate = new Date(dateParts);
        } else if (dateParts.length == 8) {
            jsDate = new Date(dateParts.substring(0, 4), dateParts.substring(4, 6) - 1, dateParts.substring(6))
        } else if (dateParts.length >= 14) {
            jsDate = new Date(dateParts.substring(0, 4), dateParts.substring(4, 6) - 1, dateParts.substring(6, 8), dateParts.substring(8, 10), dateParts.substring(10, 12), dateParts.substring(12, 14))
        }
        return jsDate.format(type);
    },
    formatNumber: function(val){
		if(isNaN(val)) return val;
    	return Number(val).toLocaleString();
	},
    removeFormat: function(val,type){
		var result = val;
			switch(type){
			case 'NUMBER' : 
				result = Number(val.replace(/,/g, ""));
				break;
		}
		
		return result;
	},
	getByte : function fnChkByte(obj,size){
		var len = obj.length;

		var rbyte = 0;
		var rStr = "";
		for(var i=0; i<len; i++){
			if(escape(obj.charAt(i)).length > 4){
    			rbyte += 2;                                         //한글2Byte
			}else{
    			rbyte++;                                            //영문 등 나머지 1Byte
			}
			if(rbyte <= size){
			    rStr += obj.charAt(i);                                      //return할 문자열 갯수
			}
		}
		
		return rStr;
	},
	getByteLength : function fnChkByte(obj){
		var len = obj.length;

		var rbyte = 0;
		for(var i=0; i<len; i++){
			if(escape(obj.charAt(i)).length > 4){
    			rbyte += 2;                                         //한글2Byte
			}else{
    			rbyte++;                                            //영문 등 나머지 1Byte
			}
		}
		
		return rbyte;
	},
	lpad : function(str, padLen, padStr) {
	    if (padStr.length > padLen) {
	        console.log("오류 : 채우고자 하는 문자열이 요청 길이보다 큽니다");
	        return str;
	    }
	    str += ""; // 문자로
	    padStr += ""; // 문자로
	    while (str.length < padLen)
	        str = padStr + str;
	    str = str.length >= padLen ? str.substring(0, padLen) : str;
	    return str;
	},
	rpad : function(str, padLen, padStr) {
	    if (padStr.length > padLen) {
	        console.log("오류 : 채우고자 하는 문자열이 요청 길이보다 큽니다");
	        return str + "";
	    }
	    str += ""; // 문자로
	    padStr += ""; // 문자로
	    while (str.length < padLen)
	        str += padStr;
	    str = str.length >= padLen ? str.substring(0, padLen) : str;
	    return str;
	},
	listToTree : function(list,opts){
		const tree = [];
		list.forEach((item,idx) => {
			item.tree_idx = idx;
			item.tree_title = item[opts.title];
			if(typeof opts.disabledFc == 'function'){
				item.tree_disabled = opts.disabledFc(item);
			}
			item.tree_contents = Object.assign({},item);
			if (!item[opts.upCd]){
				item.tree_contents.tree_depth = util.null2Str(item[opts.title]);
				item.tree_contents.tree_parent = {};
				return tree.push(item);
			}

			const pIndex = list.findIndex(el => el[opts.cd] === item[opts.upCd]);
			if(pIndex<0) return;
			item.tree_contents.tree_depth = util.null2Str(list[pIndex].tree_contents.tree_depth) +'>'+ item[opts.title];
			item.tree_contents.tree_parent = list[pIndex].tree_contents;
			if (!list[pIndex].children) {
				return list[pIndex].children = [item];
			}
			  
			list[pIndex].children.push(item);
		});
		
		if(opts.rootFlag == 'ALL') return tree;
		return tree[0];
	},
	getMobileOs : function(){//모바일 기기 os
		var os = 'other';
		var useragent = navigator.userAgent.toLowerCase();

		if(useragent.indexOf('android') > -1){
			os = 'android';
		}else if(useragent.indexOf("iphone") > -1 || useragent.indexOf("ipad") > -1 || useragent.indexOf("ipod") > -1){
			os = 'android';
		}
		return os;
	},
	getMobileDevice : function(){//모바일 기기 제조사 체크
		var target = '';
		var useragent = navigator.userAgent.toLowerCase();

		var devices = ['iphone', 'ipod', 'ipad', 'android', 'blackberry', 'windows ce', 'nokia', 'webos', 'opera mini', 'samsung', 'lg', 'mi', 'sonyericsson', 
			'opera mobi', 'iemobile', 'huawei', 'oppo', 'mot'];

		devices.forEach(function(device){
			if(useragent.indexOf(device) >= 0){
				target = device;
			}
		});
		return target;
	},
	checkFileExt : function(arg,exts){
		var exts    = exts || new Array("jsp", "cgi", "php", "asp", "aspx", "exe", "com", "html", "htm", "cab", "php3", "pl", "java", "class", "js", "css") ;
		var result  = false ;
		var sExtNm  = arg.substring(arg.lastIndexOf(".")+1) ;
			if ( (sExtNm != null) && (util.trim(sExtNm).length != 0) ){
				for (var i = 0; i < exts.length; i++){
					if (sExtNm.toUpperCase() == exts[i].toUpperCase()){
						result = true ;
						break ;
					}
				}
			}
		
		return result;
	},
	trim : function(arg){
 		arg = new String(arg) ;
 		return arg.replace(/(^ *)|( *$)/g, "") ;
	},
	maks : function (str,type){
	    var preMaskingData = str;
	    var MaskingData = "";
	    var len = "";
	
		if(type == 'EMAIL'){
			// 이메일 마스킹
			// 원본   : email1234@daum.net
			// 마스킹 : email1***@daum.net
			var emailMatchValue = preMaskingData.match(/([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\.[a-zA-Z0-9._-]+)/gi);
	
			if(util.isEmpty(emailMatchValue)){
				MaskingData = preMaskingData;
			}else{
				len = emailMatchValue.toString().split('@').length;
				MaskingData = preMaskingData.toString().replace(new RegExp('.(?=.{0,' + len + '}@)', 'gi'), '*');
			}
		}else if(type == 'PHONE'){
			// 전화번호 마스킹
			// 010-0000-0000
			// 010-****-0000 
			var phoneMatchValue = preMaskingData.match(/\d{2,3}-\d{3,4}-\d{4}/gi);
	
			// 00-000-0000 형태인 경우
			if(/-[0-9]{3}-/.test(phoneMatchValue)){
				MaskingData= phoneMatchValue.toString().replace(phoneMatchValue, phoneMatchValue.toString()
							 .replace(/-[0-9]{3}-/g, "-***-"));
			 
			}
			// 00-0000-0000 형태인 경우
			else if(/-[0-9]{4}-/.test(phoneMatchValue)){
				MaskingData= phoneMatchValue.toString().replace(phoneMatchValue, phoneMatchValue.toString()
							 .replace(/-[0-9]{4}-/g, "-****-"));
			 
			}
	
			if(util.isEmpty(MaskingData)){
				// - 없을 경우
				// 0101231234 , 01012345678
				phoneMatchValue = preMaskingData.length < 11 ? preMaskingData.match(/\d{10}/gi) : preMaskingData.match(/\d{11}/gi);
				if(util.isEmpty(phoneMatchValue)){
					MaskingData = preMaskingData;
				}else{
					if(preMaskingData.length < 11){
						MaskingData = preMaskingData.toString().replace(phoneMatchValue, phoneMatchValue.toString()
									  .replace(/(\d{3})(\d{3})(\d{4})/gi,'$1***$3'));
					}else{
						MaskingData = preMaskingData.toString().replace(phoneMatchValue, phoneMatchValue.toString()
									  .replace(/(\d{3})(\d{4})(\d{4})/gi,'$1****$3'));
					}
				}
			}
		}else if(type == 'NAME'){
			// 이름 마스킹
			// 원본   : 김두한
			// 마스킹 : 김*한
			if(util.isEmpty(preMaskingData)){
				MaskingData = preMaskingData;
			}else{
				  if (preMaskingData.length > 2) {
					var originName = preMaskingData.split('');
					originName.forEach(function(name, i) {
					  if (i === 0 || i === originName.length - 1) return;
					  originName[i] = '*';
					});
					var joinName = originName.join();
					MaskingData = joinName.replace(/,/g, '');
				  } else {
					var pattern = /.$/; // 정규식
					MaskingData = preMaskingData.replace(pattern, '*');
				  }
			}
		}
	
	    return MaskingData;
	}

}

var validate = {
	message : {
		alpha: "{_field_} 한글 제외 문자를 입력 해주세요",
		alpha_num: "{_field_}은(는) 숫자 만 사용할 수 있습니다",
		alpha_dash: "{_field_}은(는) 숫자와 하이픈 및 밑줄 만 사용할 수 있습니다",
		alpha_spaces: "{_field_}은(는) 알파벳이나 공백 사용할 수 있습니다",
		between: "{_field_}은(는) {min}에서 {max} 사이 여야합니다",
		confirmed: "{_field_}가 일치하지 않습니다",
		digits: "{_field_}은(는) {length} 자리 숫자이어야합니다",
		dimensions: "{_field_}은(는) 폭 {width} px 높이 {height} px 이내 여야합니다",
		email: "{_field_} 이메일 형식에 맞지 않습니다.",
		mobile: "{_field_} 휴대폰 형식에 맞지 않습니다.",
		notInclude: "{_field_}은(는) [{char}]를 포함 할 수 없습니다.",
		excluded: "{_field_}은(는) 유효하지 않습니다",
		ext: "{_field_}은(는) 유효한 파일 형식은 없습니다",
		image: "{_field_}은(는) 올바른 이미지 형식이 아닙니다",
		length: "{_field_}은(는) {length} 자이어야합니다",
		max_value: "{_field_}은(는) {max} 이하 이어야 합니다",
		max: "{_field_}은(는) {length} 자 이하 이어야 합니다",
		max_byte: "{_field_}은(는) {max}byte 이하 이어야 합니다",
		mimes: "{_field_}은(는) 유효한 파일 형식은 없습니다",
		min_value: "{_field_}은(는) {min} 이상 이어야 합니다",
		min: "{_field_}은(는) {length} 자 이상 이어야 합니다",
		numeric: "{_field_}은(는) 숫자 만 사용할 수 있습니다",
		oneOf: "{_field_}은(는) 유효한 값이 아닙니다",
		regex: "{_field_}의 형식이 올바르지 않습니다",
		required: "{_field_}은(는) 필수 항목입니다",
		required_if: "{_field_}은(는) 필수 항목입니다",
		size: "{_field_}은(는) {size} Byte 이내 여야합니다",
		password : "{_field_}은(는) 안전하지 않습니다. 6~12자 영문, 숫자, 특수문자를 혼용하여  입력하여 주십시오."
  	},
  	check : function(rules,val,callbackFc){
    	var messages = [];
    	var result = true;
    	var val = util.null2Str(val);
    	if(rules == null || rules.length < 1){
			if(typeof callbackFc === 'function') callbackFc(result,messages);
			return true;
		}
      	
      	rules.forEach(function(ruleStr, idx){
      		var rule = ruleStr.split(':');
      		switch(rule[0]){
      			case 'required':
      				if(util.isEmpty(val)){
      					result = false;
      					messages.push(validate.message[rule[0]]);
      				}
      				break;
      			case 'min':			
      				if(val.length < Number(rule[1])){
      					result = false;
      					messages.push(validate.message[rule[0]].replace(/\{length}/g,rule[1]));
      				}
      				break;
      			case 'max':			
      				if(val.length > Number(rule[1])){
      					result = false;
      					messages.push(validate.message[rule[0]].replace(/\{length}/g,rule[1]));
      				}
      				break;
      			case 'email':
      				if(!util.checkFormat(val,'EMAIL')){
      					result = false;
      					messages.push(validate.message[rule[0]]);
      				}
      				break;
      			case 'mobile':
      				if(!util.checkFormat(val,'HP')){
      					result = false;
      					messages.push(validate.message[rule[0]]);
      				}
      				break;
      			case 'password':
      				if(!util.checkPwd(val)){
      					result = false;
      					messages.push(validate.message[rule[0]]);
      				}
      				break;
      			case 'size':		
      				if(util.getByteLength(val) > Number(rule[1])){
      					result = false;
      					messages.push(validate.message[rule[0]].replace(/\{size}/g,rule[1]));
      				}
      				break;
      			case 'numeric':		
      				if(!util.checkFormat(val,'NUMBER')){
      					result = false;
      					messages.push(validate.message[rule[0]]);
      				}
      				break;
      			case 'digits':		
      				if(!(util.checkFormat(val,'NUMBER') && (util.getByteLength(val.toString()) == Number(rule[1])))){
      					result = false;
      					messages.push(validate.message[rule[0]].replace(/\{length}/g,rule[1]));
      				}
      				break;
      			case 'between':		
      				var arr = rule[1].split(',');
      				if(!util.checkFormat(val,'NUMBER') || (Number(val) < Number(arr[0]) || Number(val) > Number(arr[1]))){
      					result = false;
      					messages.push(validate.message[rule[0]].replace(/\{min}/g,arr[0]).replace(/\{max}/g,arr[1]));
      				}
      				break;
      			case 'max_byte':
      				if(util.getByteLength(val) > Number(rule[1])){
      					result = false;
      					messages.push(validate.message[rule[0]].replace(/\{max}/g,rule[1]));
      				}
      				break;
      			case 'notInclude':	
      				var arr = rule[1].split(',');
      				for(i=0;i<arr.length;i++){
						if(val.indexOf(arr[i]) > -1){
							result = false;
      						messages.push(validate.message[rule[0]].replace(/\{char}/g,rule[1]));
							break;
						}
					}
      				break;
      		}
      	});
      	
      	if(typeof callbackFc === 'function') callbackFc(result,messages);
      	return result;
	},
  	checkField : function(rules,val,title){
		var _result = true;
		var _message = '';
    	validate.check(rules,val,function(result,messages){   
	      	_result = result;
	      	_message = messages.join('<br>').replace(/\{_field_}/g,'['+title+']');
    	});
    	
    	if(!_result) alert(_message);
    	return _result;
	}
	
};

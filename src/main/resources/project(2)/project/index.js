var socket;

if (!window.WebSocket) {
	window.WebSocket = window.MozWebSocket;
}
if (window.WebSocket) {
	socket = new WebSocket("wss://ourclipboard.tk/ws");
	socket.onmessage = function(event) {
		console.log(event);
		if (event.data.substr(5, 8).length == 8 && !isNaN(event.data.substr(5, 8) - 0)) {
			$('#LoginCodeText').val(event.data.substr(5, 8));
		} else if (event.data == $('#textarea').val() && event.data != '') {
			$('#responseText').val('发送成功');
			$('#textarea').val('');
			let ul = document.getElementById('textHistory');
			var li = document.createElement('li');
			li.innerHTML = event.data;
			ul.removeChild(ul.children[4]);
			ul.insertBefore(li, ul.children[0]);
			// for (var i = 4; i > 0; i--) {
			// 	lis[i].value = lis[i - 1].value;
			// }
			// lis[0].value = event.data;
			// console.log(event.data);
			// console.log(lis[0].value);
		} else {
			$('#responseText').val(event.data);
		}
		console.log("接收到消息");

		heartCheck.start();
	};
	socket.onopen = function(event) {
		console.log("连接开启");
		console.log(event);
		var ta = document.getElementById('responseText');
		document.getElementById('loginPage').style.display = "none";
		document.getElementById('card').style.display = "block";
		
		ta.value = "连接开启!";
		heartCheck.start();
	};
	socket.onclose = function(event) {
		console.log(event);
		var ta = document.getElementById('responseText');
		ta.value = "连接被关闭,尝试重连";
		reconnect("wss://ourclipboard.tk/ws");
	};
} else {
	alert("你的浏览器不支持 WebSocket！");
}

function login(value) {
	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {
		let message = {
			type: "login",
			token: value
		}
		socket.send(JSON.stringify(message));
		$('#responseText').val("登陆中...");
	} else {
		alert("连接没有开启.");
	}
}
var code;

function getLoginCode() {
	timeout(document.getElementById('basic-addon'));
	if (!window.WebSocket) {
		return;
	}

	if (socket.readyState == WebSocket.OPEN) {
		let logging = {
			type: "code",
			token: null
		}
		socket.send(JSON.stringify(logging));
		
	} else {
		alert("连接没有开启");
	}
}

function getHistory(code) {
	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {
		let history = {
			type: "history",
			token: code
		}
		socket.send(JSON.stringify(history));
	} else {
		alert("连接没有开启");
	}
}

function sendGroup(code, value) {
	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {
		var message = {
			type: "sendGroup",
			token: code,
			value: value
		}
		socket.send(JSON.stringify(message));
	} else {
		alert("连接没有开启.");
	}
}

var lockReconnect = false;
var tt;
//重连
function reconnect(url) {
	if (lockReconnect) {
		return;
	};
	lockReconnect = true;
	//没连接上会一直重连，设置延迟避免请求过多
	tt && clearTimeout(tt);
	tt = setTimeout(function() {
		socket = new WebSocket("wss://ourclipboard.tk/ws");
		$('#responseText').val("重连中...");
		lockReconnect = false;
	}, 4000);
}

//心跳检测
var heartCheck = {
	timeout: 5000,
	timeoutObj: null,
	serverTimeoutObj: null,
	start: function() {
		console.log('start');
		var self = this;
		this.timeoutObj && clearTimeout(this.timeoutObj);
		this.serverTimeoutObj && clearTimeout(this.serverTimeoutObj);
		this.timeoutObj = setTimeout(function() {
			//这里发送一个心跳，后端收到后，返回一个心跳消息，
			var message = {
				type: "heartCheck",
				value: null
			}
			socket.send(JSON.stringify(message));
			self.serverTimeoutObj = setTimeout(function() {
				console.log(111);
				//                   socket.close();
			}, self.timeout);

		}, this.timeout)
	}
}
// 倒计时
var wait = 15;
function timeout(o) {
	if (wait == 0) {
		o.removeAttribute("disabled");
		o.value = "获取登录码";
		wait = 15;
		o.style.backgroundColor =" #4682B4";
	} else {
		o.style.backgroundColor =" gray";
		o.setAttribute("disabled", true);
		o.value = "等待" + wait + "秒";
		wait--;
		setTimeout(function() {
				timeout(o)
			},
			1000)
	}
}



window.onbeforeunload = function(event) {
	event.returnValue = "刷新提醒";
};

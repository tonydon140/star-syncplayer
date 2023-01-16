// ==UserScript==
// @name         StarSyncplayerScript
// @namespace    https://bbs.tampermonkey.net.cn/
// @version      0.1.0
// @description  【星星同步播放脚本】，一款可以同步播放视频的脚本，支持腾讯视频、Bilbili、YouTuBe、优酷
// @author       TonyDon
// @match        https://v.qq.com/x/cover*
// @match        https://www.bilibili.com/video/*
// @match        https://www.bilibili.com/bangumi/play/*
// @match        https://www.youtube.com/watch*
// @match        https://v.youku.com/v_show/*
// ==/UserScript==

(function () {
    'use strict';

    // 服务器URL
    const SERVER_URL = 'ws://localhost:6515/syncplayer'
    // const SERVER_URL = 'wss://www.tonydon.top:6515/movie'

    // 网站类型
    const WEBSITE_TYPE = {
        BILIBILI: 0,
        TX: 1,
        YOUTUBE: 2,
        YOUKU: 3
    }

    // 消息类型
    const MESSAGE_TYPE = {
        ACTION: 101,
        STRING: 102,
        MOVIE: 103
    }
    // 动作码
    const ACTION_CODE = {
        UNBIND: 101,
        OFFLINE: 102,

        MOVIE_PLAY: 301,
        MOVIE_PAUSE: 302,
        MOVIE_SYNC: 304,

        BIND: 201,
        CONNECTED: 202,
        SERVER_RESPONSE: 204
    };


    let website = WEBSITE_TYPE.BILIBILI;
    let url = window.location.href
    let isBind = false;
    let client = new WebSocket(SERVER_URL);

    // 判断网站的类型
    if (url.includes("v.qq.com")) {
        website = WEBSITE_TYPE.TX;
    } else if (url.includes("bilibili")) {
        website = WEBSITE_TYPE.BILIBILI;
    } else if (url.includes("youtube")) {
        website = WEBSITE_TYPE.YOUTUBE;
    } else if (url.includes("youku")) {
        website = WEBSITE_TYPE.YOUKU;
    }


    // 获取视频组件
    function getVideo() {
        if (website === WEBSITE_TYPE.TX) {
            return document.querySelector(".txp_videos_container").children[1];
        } else if (website === WEBSITE_TYPE.BILIBILI) {
            return document.querySelector(".bpx-player-video-wrap video");
        } else if (website === WEBSITE_TYPE.YOUTUBE) {
            return document.querySelector(".html5-main-video");
        } else if (website === WEBSITE_TYPE.YOUKU) {
            return document.querySelector(".video-layer video");
        }
    }


    // 处理消息
    client.onmessage = function (ev) {
        let message = JSON.parse(ev.data);
        let data = JSON.parse(message.json);

        if (message.type === MESSAGE_TYPE.ACTION) {
            doAction(data['actionCode'])
        } else if (message.type === MESSAGE_TYPE.STRING) {
            doString(data['actionCode'], data['content'])
        } else if (message.type === MESSAGE_TYPE.MOVIE) {
            doMovie(data);
        }
    }

    client.onerror = function (ev) {
        console.log(ev)
    }

    let friendIdInput = undefined;
    let bindButton = undefined;
    let pauseButton = undefined;
    let playButton = undefined;
    let syncButton = undefined;

    function doAction(actionCode) {
        if (actionCode === ACTION_CODE.UNBIND) {
            doUnBind();
        } else if (actionCode === ACTION_CODE.OFFLINE) {
            doUnBind();
        }
    }

    function doString(actionCode, content) {
        if (actionCode === ACTION_CODE.CONNECTED) {
            doConnected(content)
        } else if (actionCode === ACTION_CODE.BIND) {
            doBind(content);
        } else if (actionCode === ACTION_CODE.SERVER_RESPONSE) {
            alert(content);
        }
    }

    function doMovie(data) {
        let video = getVideo();
        if (data.actionCode === ACTION_CODE.MOVIE_PLAY) {
            video.play();
        } else if (data.actionCode === ACTION_CODE.MOVIE_PAUSE) {
            video.pause();
        } else if (data.actionCode === ACTION_CODE.MOVIE_SYNC) {
            video.pause();
            video.currentTime = data.seconds;
            video.playbackRate = data.rate;
            video.play();
        }
    }

    // 创建面板
    function doConnected(id) {
        const template = `
            <div style="background-color: antiquewhite; padding: 5px; display: inline-block;position: fixed;bottom: 50px;z-index: 99999">
                <label>
                    ${id}
                    <input id="friendIdInput" maxlength="6" type="text" placeholder="她/他的星星号" style="width: 100px; outline: none">
                </label>
                <button id="bindButton">绑定</button>
                <button id="playButton">播放</button>
                <button id="pauseButton">暂停</button>
                <button id="syncButton" disabled>同步</button>
            </div>
        `;

        let tempNode = document.createElement('div');
        tempNode.innerHTML = template;
        document.body.appendChild(tempNode);

        friendIdInput = tempNode.querySelector("#friendIdInput");
        bindButton = tempNode.querySelector("#bindButton");
        pauseButton = tempNode.querySelector("#pauseButton");
        playButton = tempNode.querySelector("#playButton");
        syncButton = tempNode.querySelector("#syncButton");


        // 绑定按钮
        bindButton.onclick = function () {
            if (isBind) {
                // 已经绑定了,则进行解绑
                doUnBind();
                sendActionMessage(ACTION_CODE.UNBIND)
            } else {
                // 还没有绑定,则进行绑定
                let friendId = friendIdInput.value;

                // 校验星星号
                if (friendId.length < 8) {
                    alert("星星号必须是8位纯数字");
                    return;
                }
                for (let ch of friendId) {
                    if (ch < '0' || ch > '9') {
                        alert("星星号必须是8位纯数字");
                        return;
                    }
                }
                sendStringMessage(ACTION_CODE.BIND, friendId)
            }
        }

        // 播放按钮
        playButton.onclick = function () {
            if (isBind) {
                sendMovieMessage(ACTION_CODE.MOVIE_PLAY)
            } else {
                getVideo().play();
            }
        }

        // 暂停按钮
        pauseButton.onclick = function () {
            if (isBind) {
                sendMovieMessage(ACTION_CODE.MOVIE_PAUSE);
            } else {
                getVideo().pause();
            }
        }

        // 同步按钮
        syncButton.onclick = function () {
            let video = getVideo();
            sendMovieMessage(ACTION_CODE.MOVIE_SYNC, video.currentTime, video.playbackRate);
        }
    }


    function sendActionMessage(actionCode) {
        let message = `{"type":101,"json":"{\\"actionCode\\":${actionCode}}"}`
        client.send(message)
    }

    function sendStringMessage(actionCode, content) {
        let message = `{"type":102,"json":"{\\"actionCode\\":${actionCode},\\"content\\":\\"${content}\\"}"}`
        client.send(message)
    }

    // 发送电影消息
    function sendMovieMessage(actionCode, seconds, rate) {
        let json = {
            'actionCode': actionCode,
            'seconds': seconds,
            'rate': rate
        };
        let message = {
            'type': MESSAGE_TYPE.MOVIE,
            'json': JSON.stringify(json)
        };
        console.log(JSON.stringify(message))
        client.send(JSON.stringify(message));
    }

    // 处理绑定消息
    function doBind(number) {
        isBind = true;
        friendIdInput.value = number;
        bindButton.innerText = "解除绑定";
        playButton.innerText = '一起播放';
        pauseButton.innerText = '一起暂停';
        syncButton.disabled = false;
    }

    // 处理解绑消息
    function doUnBind() {
        isBind = false;
        friendIdInput.value = "";
        bindButton.innerText = "绑定";
        playButton.innerText = '播放';
        pauseButton.innerText = '暂停';
        syncButton.disabled = true;
    }


})();
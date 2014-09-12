window.innityapps_invitationY = -1;
window.innityapps_receiveMessage = function(event)
{
	var msg = event.data.toString();
	var arrMessage = msg.split(':');
	if(arrMessage[0] == 'adType')
	{
console.log('adType:'+arrMessage[1]);
		window.innityapps_setAdLayout(parseInt(arrMessage[1]));
	}
	else if(arrMessage[0] == 'x')
	{
console.log('x: '+arrMessage[1]);
	}
	else if(arrMessage[0] == 'y')
	{
console.log('y: '+arrMessage[1]);
	}
	else if(arrMessage[0] == 'setInvitationY')
	{
console.log('setInvitationY:'+arrMessage[1]);
		window.innityapps_invitationY = parseInt(arrMessage[1]);
	}
	else if(arrMessage[0] == 'adHeight')
	{
console.log('adHeight:'+arrMessage[1]);
		window.innityapps_setBannerHeight(parseInt(arrMessage[1]));
		window.innityapps_setAdLayout(window.innityapps_bannerFormat);
	}
	else if(arrMessage[0] == 'availableWidth')
	{
console.log('availableWidth:'+arrMessage[1]);
		window.innityapps_setBannerWidth(parseInt(arrMessage[1]));
		window.innityapps_setAdLayout(window.innityapps_bannerFormat);
	}
	else if(arrMessage[0] == 'availableHeight')
	{
console.log('availableHeight:'+arrMessage[1]);
		window.innityapps_setBannerHeight(parseInt(arrMessage[1]));
		window.innityapps_setAdLayout(window.innityapps_bannerFormat);
	}
	else if(msg == 'adDidAppear')
	{
console.log('adDidAppear');
	}
	else if(msg == 'adDidDisappear')
	{
console.log('adDidDisappear');
	}
	else if(msg == 'adDidBeginPreview')
	{
console.log('adDidBeginPreview');
	}
	else if(msg == 'adDidEndPreview')
	{
console.log('adDidEndPreview');
	}
	else if(msg == 'adDidExpand')
	{
console.log('adDidExpand');
//		document.getElementById('invitation').style.display = 'hidden';
		window.innityapps_adDidExpand();
	}
	else if(msg == 'adDidCloseExpanded')
	{
console.log('adDidCloseExpanded');
		document.getElementById('invitation').style.display = '';
	}
	if(event.data == 'showParams')
	{
		alert(location.search);
	}
	// Execute the relevant functions
}

window.innityapps_execCommand=function(cmd)
{
	var iframe = document.createElement("IFRAME");
	iframe.setAttribute("src", "appsploration://"+cmd);
	document.documentElement.appendChild(iframe);
	iframe.parentNode.removeChild(iframe);
	iframe = null;
}

window.innityapps_setAdLayout=function(adType)
{
	var invitation = document.getElementById('invitation').style;
	var banner = document.getElementById('banner').style;
	switch(adType)
	{
		case 1:	// Pull up
			invitation.left = '0px';
			invitation.top = '0px';
			banner.left = '0px';
			banner.top = '50px';
			break;
		case 2:	// Pull down
			banner.left = '0px';
			banner.top = '0px';
			invitation.left = '0px';
			invitation.top = window.innityapps_adHeight + 'px';
			break;
		case 3:	// Bottom swipe left
			invitation.left = '0px';
			if(window.innityapps_invitationY > -1)
				invitation.top = window.innityapps_invitationY + 'px';
			else
				invitation.top = (window.innityapps_adHeight-50) + 'px';
//anrdoid
//			banner.left = '320px';
			banner.left = window.innityapps_adWidth+'px';
			banner.top = '0px';
			break;
		case 4:	// Bottom swipe right
			banner.left = '0px';
			banner.top = '0px';
//android
//			invitation.left = '320px';
			invitation.left = window.innityapps_adWidth+'px';
			if(window.innityapps_invitationY > -1)
				invitation.top = window.innityapps_invitationY + 'px';
			else
				invitation.top = (innityapps_adHeight-50) + 'px';
			break;
		case 5:	// Top swipe left
			invitation.left = '0px';
			if(window.innityapps_invitationY > -1)
				invitation.top = window.innityapps_invitationY + 'px';
			else
				invitation.top = '0px';
			banner.left = '320px';
			banner.top = '0px';
			break;
		case 6:	// Top swipe right
			banner.left = '0px';
			banner.top = '0px';
			invitation.left = '320px';
			if(window.innityapps_invitationY > -1)
				invitation.top = window.innityapps_invitationY + 'px';
			else
				invitation.top = '0px';
			break;
		case 7:	// Interstitial
			invitation.display = 'none';
			banner.left = '0px';
			banner.top = '0px';
			break;
		case 8:	// Tap to expand
			invitation.left = '0px';
			invitation.top = '0px';
			banner.left = '0px';
			banner.top = '50px';
			break;
		case 11:	// Tap to expand
			invitation.left = '0px';
			invitation.top = '0px';
			banner.left = '0px';
			banner.top = '250px';
			break;
	}
}

function innityapps_parseURLParams()
{
	var strURLParams = location.search;
//	var strURLParams = '?f=e&targeting=gender:male|color:red&g=e';

	var arrTargeting = {};
	var paramName = 'targeting=';
	var startPos = strURLParams.indexOf(paramName);
	if(startPos != -1)
	{
		startPos += paramName.length;
		var endPos = strURLParams.indexOf('&', startPos);
		var strTargetingParams;
		if(endPos == -1)
		{
			strTargetingParams = strURLParams.substr(startPos);
		}
		else
		{
			strTargetingParams = strURLParams.substr(startPos, endPos-startPos);
		}

		var arrTargetingParams = strTargetingParams.split('|');
        var tmpArr;
        for(var i=0; i<arrTargetingParams.length; i++)
        {
            tmpArr = arrTargetingParams[i].split(':');
            arrTargeting[tmpArr[0]] = tmpArr[1];
        }
        return arrTargeting;
	}
	return null;
}

function getTargetingParam(key)
{

}

window.innityapps_arrTargeting = {};

window.onload=function()
{
	if (window.addEventListener){
		addEventListener("message", innityapps_receiveMessage, false)
	} else {
		attachEvent("onmessage", innityapps_receiveMessage)
	}

	window.open=function(url, name, properties)
	{
		window.innityapps_execCommand('open?url='+encodeURIComponent(url).replace(/'/g,"%27").replace(/"/g,"%22"));
//		window.innityapps_execCommand('open?url='+encodeURI(url));
	}

	window.innityapps_arrTargeting = window.innityapps_parseURLParams();
	/*
	if(window.arrTargeting)
	{
		alert(innityapps_arrTargeting['gender']);
	}
	*/
	
// android
/*
window.innityapps_setBannerWidth(460);
window.innityapps_setAdLayout(window.innityapps_bannerFormat);
window.innityapps_setBannerHeight(800);
window.innityapps_setAdLayout(window.innityapps_bannerFormat);
window.innityapps_adDidExpand();
*/

	window.innityapps_execCommand('adLoaded?bannerFormat='+window.innityapps_bannerFormat+'|');
};
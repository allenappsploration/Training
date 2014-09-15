window.innityapps_receiveMessage = function(event)
{
//	if (event.origin !== "https://4926-cdn-media-innity-net.voxcdn.com")
//		return;
	document.getElementById('innity_debugDiv').innerHTML = event.data;
	var msg = event.data.toString();
	var arrMessage = msg.split(':');
	if(arrMessage[0] == 'adType')
	{
		window.innityapps_setAdLayout(parseInt(arrMessage[1]));
	}
	else if(msg == 'adDidExpand')
	{
//		document.getElementById('invitation').style.display = 'hidden';
		window.innityapps_adDidExpand();
	}
	else if(msg == 'adDidCloseExpanded')
	{
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
			invitation.top = '460px';
			break;
		case 3:	// Bottom swipe left
			invitation.left = '0px';
			invitation.top = '410px';
			banner.left = '320px';
			banner.top = '0px';
			break;
		case 4:	// Bottom swipe right
			banner.left = '0px';
			banner.top = '0px';
			invitation.left = '320px';
			invitation.top = '410px';
			break;
		case 5:	// Top swipe left
			invitation.left = '0px';
			invitation.top = '0px';
			banner.left = '320px';
			banner.top = '0px';
			break;
		case 6:	// Top swipe right
			banner.left = '0px';
			banner.top = '0px';
			invitation.left = '320px';
			invitation.top = '0px';
			break;
		case 7:	// Interstitial
			invitation.display = 'none';
			break;
		case 8:	// Tap to expand
			invitation.left = '0px';
			invitation.top = '0px';
			banner.left = '0px';
			banner.top = '50px';
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
	var debugDiv = document.createElement('DIV');

	debugDiv.id = 'innity_debugDiv';
	debugDiv.style.position = 'fixed';
	debugDiv.style.top = '0px';
	debugDiv.style.left = '0px';
	debugDiv.innerHTML = 'innity_debugDiv';
	debugDiv.style.display = 'none';
	document.documentElement.appendChild(debugDiv);

	window.innityapps_arrTargeting = window.innityapps_parseURLParams();
	/*
	if(window.arrTargeting)
	{
		alert(innityapps_arrTargeting['gender']);
	}
	*/
	window.innityapps_execCommand('adLoaded');
};
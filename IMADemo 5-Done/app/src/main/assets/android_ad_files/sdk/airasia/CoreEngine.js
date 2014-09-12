var getParam = '';

var bannerData = {};

/**
 * @type PreviewEngine
 */
var banner;

var easeHelper;
var fontHelper;

function initComposer() {
	getParam = queryString();
	easeHelper = new PEaseHelper();
	fontHelper = new PFontHelper();
	separateData();
	initPreviewEngine();
};

function jumpToTime(time, componentType) {
	if(time == 17) {
		_showMainBanner();
		return;
	}

	var frame = time * 60;
	banner.gotoFrame(frame);

	function _showMainBanner() {
		var kineticEl = document.getElementsByClassName('kineticjs-content')[0];
		kineticEl.style.display = 'none';
		var anotherEl = document.getElementById('another-banner');
		anotherEl.style.display = '';
	};
};

/**
 * Separate data into each canvas.
 */
function separateData() {
	var TYPE_BANNER = 1;

	for(var i = 0; i < importedJSONObject.length; i++) {
		if(importedJSONObject[i]['component_type'] == TYPE_BANNER) {
			bannerData = importedJSONObject[i];
		}
	}
};

function initPreviewEngine() {
	banner = new PreviewEngine(bannerData, 'p5banner', 'banner');

	var dcoInfo = {};
//	if((typeof dcoData) !== 'undefined') {
//		dcoInfo = dcoData.getData();
//		var dcoTrack = dcoData.getParam();
//		if(dcoTrack.length > 0) {
//			InnityHTMLAd.setDCO(dcoTrack);
//		}
//	}
	banner.fromDCO(dcoInfo);
};
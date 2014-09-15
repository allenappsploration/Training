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
	var frame = time * 60;
	banner.gotoFrame(frame);
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
	banner = new PreviewEngine(bannerData, 'p5banner', 'banner', true, animationEnd);

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

function animationEnd() {
	var anotherEl = document.getElementById('another-banner');
	anotherEl.style.display = '';
};
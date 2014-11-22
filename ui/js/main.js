var HelloSearch = function(form) {
	this.form = $(form);
	this.resultTemplate = $('.result').clone();
	this.searched = false;
};
HelloSearch.prototype = {
	constructor: HelloSearch,
	init: function() {
		$('.result').remove();
		var self = this;
		self.form.submit(function(e) {
			e.preventDefault();
			self.search();
		});
	},
	search: function() {
		var self = this;
		if(self.searched) $('.result').remove();
		$.ajax({
			url: self.form.attr('action'),
			type: "GET",
			data: { query: self.form.find('input').val() },
			success: self.showResults.bind(self)
		});
	},
	showResults: function(data) {
		var self = this;
		console.log(data);
		data = JSON.parse(data);
		var results = data.results;
		var query_str = data.query_str;
		var has_query = data.has_query;

		$('.results-count').text(results.length);
		$('.results-time').text(data.finished_time);
		
		$.each(results, function(index, result) {
			$('.results').append(self.makeResult(result));
		});

		$('.results').fadeIn();
		this.searched = true;
	},
	makeResult: function(result) {
		var self = this;
		var newResult = self.resultTemplate.clone();
		newResult.find('.result-title').text(result.pageTitle);
		newResult.find('.result-url').text(result.url);
		newResult.find('.result-modified').text(result.lastModified);
		newResult.find('.result-score').text(parseFloat(result.score).toFixed(2));
		newResult.find('.result-size').text(result.size);	
		return newResult;
	}
}
$(document).ready(function() {
	var hs = new HelloSearch('form.search');
	hs.init();
});
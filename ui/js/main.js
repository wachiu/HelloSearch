var HelloHistory = function() {
	this.storedHistory;
}

HelloHistory.prototype = {
	constructor: HelloHistory,
	init: function() {
		this.add("This is a test search!");
		this.populate();
	},
	populate: function() {
		$('.history-list').html();
		var storedHistory = this.retrieve();
		console.log(storedHistory);
		$.each(storedHistory, function(index, item) {
			var date = new Date(item.searchDate);
			$('.history-list').append("<li>" + item.query + " <span>" + date.getMonth() + "</span></li>");
		});
	},
	retrieve: function() {
		return JSON.parse(localStorage.getItem('helloHistory'));
	},
	store: function(string) {
		localStorage.setItem('helloHistory', string);
	},
	add: function(query) {
		var storedHistory = this.retrieve();
		if(storedHistory == null) {
			storedHistory = [{
				searchDate: Date.now(),
				query: query
			}];
		} else {
			storedHistory.push({
				searchDate: Date.now(),
				query: query
			});
		}
		this.store(JSON.stringify(storedHistory));
	},
	clear: function() {
		localStorage.setItem('helloHistory', null);
	}
}

var transitionEnd = 'transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd';
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
		var input = self.form.find('input');
		if(input.val() == "") {

		} else {
			$.ajax({
				url: self.form.attr('action'),
				type: "GET",
				data: { query: self.form.find('input').val() },
				success: self.showResults.bind(self),
				beforeSend: function() {
					$('.searching').stop(true).fadeIn();
					$('.results').stop(true).fadeOut(function() {
						$('.result').remove();
					});
				},
				complete: function() {
					$('.searching').stop(true).fadeOut();
				}
			});
		}
	},
	showResults: function(data) {
		var self = this;
		console.log(data);
		data = JSON.parse(data);
		var results = data.results;
		var query_str = data.query_str;
		var has_query = data.has_query;

		if(results) {
			$('.results-count').text(results.length);
			$('.results-time').text(data.finished_time);
			
			$.each(results, function(index, result) {
				$('.results').append(self.makeResult(result));
			});

			$('body').addClass('searched');
			if(this.searched) {
				$('.results').stop(true).fadeIn();
			} else {
				$('.container.search').bind(transitionEnd, function() {
					$(this).unbind(transitionEnd);
					$('.results').fadeIn();
				});
				this.searched = true;
			}
		}
	},
	makeResult: function(result) {
		var self = this;
		var newResult = self.resultTemplate.clone();
		newResult.find('.result-title').text(result.pageTitle);
		newResult.find('.result-url').text(result.url);
		newResult.find('.result-modified').text(result.lastModified);
		newResult.find('.result-score').text(parseFloat(result.score).toFixed(2));
		newResult.find('.result-size').text(result.size + " kb");	
		newResult.find('.result-size').text(result.size);	
		var wordFreqs = $.parseJSON(result.wordFreqs);
		newResult.find('.result-frequent').html($(wordFreqs.map(function(v) {
			v = v.split("=");
			return '<span>' + v[0] + ' <span class="badge">' + v[1] + '</span></span>';
		})).get().join(""));
		return newResult;
	}
}

$(document).ready(function() {
	var hs = new HelloSearch('form.search');
	hs.init();
	var hh = new HelloHistory();
	hh.init();
});
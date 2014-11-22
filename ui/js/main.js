var monthNames = [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ];

var HelloHistory = function() {
	this.storedHistory;
	this.itemTemplate = $('.history-item').clone();
}

HelloHistory.prototype = {
	constructor: HelloHistory,
	init: function() {
		var self = this;
		$('.history-item').remove();
		self.populate();
		$('.history-clear').click(self.clear.bind(self));
		$('nav .his').click(function() {
			$('.history').toggle();
		});
	},
	populate: function() {
		var self = this;
		$('.history-list').html("");
		var storedHistory = this.retrieve();
		if(storedHistory) {
			$.each(storedHistory, function(index, item) {
				var date = new Date(item.searchDate);
				var hours = date.getHours();
				var ampm = hours >= 12 ? 'pm' : 'am';
				var dateString = monthNames[date.getMonth()] + " " + date.getDate() + ", " + hours + ":" + date.getMinutes() + ampm;

				var newItem = self.itemTemplate.clone();
				newItem.find('.history-query').text(item.query);
				newItem.find('.history-date').text(dateString);

				$('.history-list').prepend(newItem);
			});
		} else {
			self.showMessage();
		}
		
	},
	showMessage: function() {
		$('.history-list').append("<p class='message'>You have no search history.</p>");
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
		this.populate();
	},
	clear: function() {
		$('.history-list').html("");
		this.showMessage();
		localStorage.setItem('helloHistory', null);
	}
}

var transitionEnd = 'transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd';
var HelloSearch = function(form) {
	this.form = $(form);
	this.resultTemplate = $('.result').clone();
	this.searched = false;
	this.history = new HelloHistory();
};
HelloSearch.prototype = {
	constructor: HelloSearch,
	init: function() {
		var self = this;
		self.history.init();
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
					$('.results').stop(true).fadeOut();
				},
				complete: function() {
					$('.searching').stop(true).fadeOut();
					self.history.add(input.val());
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
			$('.result').remove();
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
		} else {
			// show message 
		}
	},
	makeResult: function(result) {
		var self = this;
		var newResult = self.resultTemplate.clone();
		newResult.find('.result-title').text(result.pageTitle);
		newResult.find('.result-url').text(result.url);
		newResult.find('.result-modified').text(result.lastModified);
		newResult.find('.result-score').text(parseFloat(result.score).toFixed(2));
		newResult.find('.result-size').text(result.size + " b");	
		return newResult;
	}
}

$(document).ready(function() {
	var hs = new HelloSearch('form.search');
	hs.init();
});
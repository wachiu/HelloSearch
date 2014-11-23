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

				if(hours > 12) hours -= 12;

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
		return $.parseJSON(localStorage.getItem('helloHistory'));
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

		var query = getParameterByName('query');
		if(query != "") self.simSearch(query);

		window.onpopstate = function(event) {
			if(event.state != null) self.simSearch(event.state);
		};

		$('body').on('click', '.find-similar', function() {
			self.simSearch($(this).data('simquery'));
		});
		$('body').on('click', '.child-links, .parent-links', function(e) {
			e.preventDefault();
		});
	},
	simSearch: function(query) {
		this.form.find('input').val(query);
		this.search();
	},
	search: function() {
		var self = this;
		var input = self.form.find('input');
		if(input.val() == "") {

		} else {
			var query = self.form.find('input').val();
			$.ajax({
				url: self.form.attr('action'),
				type: "GET",
				data: { query: query },
				success: self.showResults.bind(self),
				beforeSend: function() {
					window.history.pushState(query, query + " | Hello Search", '?query=' + query);
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
		data = $.parseJSON(data);
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
		newResult.find('.result-title').text(result.pageTitle).attr('href', result.url);
		newResult.find('.result-url').text(result.url).attr('href', result.url);
		newResult.find('.result-text').html(result.documentText + "...");
		newResult.find('.result-modified span').text(result.lastModified);
		newResult.find('.result-item').text(result.matchTerms);
		newResult.find('.result-score').text(parseFloat(result.score).toFixed(2));
		newResult.find('.result-size').text(parseFloat((result.size)/1024).toFixed(2) + " KB");	

		var freqJoin = "";
		var wordFreqs = $.parseJSON(result.wordFreqs);
		newResult.find('.result-frequent').html($(wordFreqs.map(function(v) {
			v = v.split("=");
			freqJoin += v[0]+" ";
			return '<span>' + v[0] + ' <span class="badge">' + v[1] + '</span></span>';
		})).get().join(""));

		newResult.find('.find-similar').attr('data-simquery', freqJoin);

		var childLinks = "";
		$.each($.parseJSON(result.childrenLinks), function(index, value) {
			childLinks += "<a href='" + value + "'>" + value + "</a><br>";
		});
		if(childLinks == "") childLinks = "No child links."
		newResult.find('.child-links').popover({
			html:true, placement:'left', content:childLinks
		});
		var parentLinks = "";
		$.each($.parseJSON(result.parentLinks), function(index, value) {
			parentLinks += "<a href='" + value + "'>" + value + "</a><br>";
		});
		if(parentLinks == "") parentLinks = "No parent links."
		newResult.find('.parent-links').popover({
			html:true, placement:'left', content:parentLinks
		});
		return newResult;
	}
}

$(document).ready(function() {
	var hs = new HelloSearch('form.search');
	hs.init();
});

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
$(document).ready(function() {
	/*
	var titles = [
		"I had cereal for breakfast",
		"This computer is on fire",
		"The window is broken",
		"This is a pencil",
		"Water bottle is big",
		"My notebook is on fire",
		"I like my sofa",
		"This is not a result",
		"I'm going to eat diner later",
		"Dinosaurs are cool"
	]
	var aresult = $('.result').clone();
	for(var i = 0; i < 30; i++) {
		var temp = aresult.clone();
		var size = Math.ceil(Math.random() * 1000) + 1000;
		var title = titles[Math.ceil(Math.random() * 10)];
		temp.find('.result-size').text(size+' kb');
		temp.find('.result-title').text(title);
		$('.results').append(temp);
	}
	*/
	$('html,body').animate({
		scrollTop: $(".results").offset().top-50
	});
});
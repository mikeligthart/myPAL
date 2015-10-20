//Start interactive slideshow
var slide = 0;
var endSlide = 12;
var timing = [4000, 0, 0, 3500, 4500, 0, 0, 0, 2500, 3000, 3500, 3000];
var enterToContinue = [false, true, true, false, false, false, true, true, false, false, false, false];
var focus = ["", "#firstName", "#age", "", "", "", "#opinion1", "#opinion2", "", "", "", ""];
var afterChoice = false;
loadTimeout(timing[slide]);

//Go automatically to the next slide after a certain time if required
function loadTimeout(time){
    if(time > 0){
        setTimeout(function(){goToNextSlide();}, time);
    }
}

//Go to the next slide
function goToNextSlide(){
    var nextSlide = slide+1;
    $("#registrationItemSpeech" + nextSlide).trigger('load');
    if(afterChoice){
        itemTitle1 = "#registrationItem" + slide + "-0";
        itemTitle2 = "#registrationItem" + slide + "-1";
        $(itemTitle1).hide();
        $(itemTitle2).hide();
        afterChoice = false;
    }else{
        $("#registrationItem" + slide).hide();
    }
    $("#registrationItem" + nextSlide).show();
    if(focus[nextSlide] != ""){
        $(focus[nextSlide]).focus();
    }
    $("#registrationItemSpeech" + nextSlide).trigger('play');
    loadTimeout(timing[nextSlide]);
    slide = slide + 1;
}

function goToNextSlideBasedOnChoice(choice){
    var nextSlide = slide+1;
    var speechTitle = "#registrationItemSpeech" + nextSlide + "-" + choice;
    var itemTitle = "#registrationItem" + nextSlide + "-" + choice;
    $(speechTitle).trigger('load');
    $("#registrationItem" + slide).hide();
    $(itemTitle).show();
    if(focus[nextSlide] != ""){
        var focusTitle = focus[nextSlide] + "-" + choice;
        $(focusTitle).focus();
    }
    $(speechTitle).trigger('play');
    loadTimeout(timing[nextSlide]);
    slide = slide + 1;
    afterChoice = true;
}

//Button listeners
$(".registrationNext").click(function(e) {
    e.preventDefault();
    goToNextSlide();
});

$("#registrationFieldChoiceYes").click(function(e) {
    e.preventDefault();
    goToNextSlideBasedOnChoice(0);
});

$("#registrationFieldChoiceNo").click(function(e) {
    e.preventDefault();
    goToNextSlideBasedOnChoice(1);
});

//Keyboard listner
$(document).ready(function() {
  $(window).keydown(function(event){
    if( (event.keyCode == 13) && (slide != endSlide) ) {
      event.preventDefault();
      return false;
    }
  });
});

window.onkeyup = function(e) {
    var key = e.keyCode ? e.keyCode : e.which;
    if (key == 13 && enterToContinue[slide]){
        e.preventDefault();
        goToNextSlide();
        return false;
    }
}
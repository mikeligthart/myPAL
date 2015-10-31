var AvatarBehaviorManager = function (numberOfSlides) {
    this.numberOfSlides = numberOfSlides;
    this.behaviorIndex = 0;
    this.timers = [];
    this.addTimer = function(time) {
        this.timers.push(time)
    };
    this.enterToContinue = [];
    this.fillEnterToContinue = function(){
        for(i = 0; i < this.numberOfSlides; i++){
            if($("#avatarEnterButton" + i).get(0)){
                this.enterToContinue.push(true);
            } else {
                this.enterToContinue.push(false);
            }
        }
    }

    this.start = function() {
        this.behaviorIndex = 0;
        this.fillEnterToContinue();
        this.setTimer(this.timers[this.behaviorIndex]);
    };

    this.setTimer = function(time) {
        if(time > 0){
            var _this = this;
            setTimeout(function(){_this.nextBehavior();}, time);
        }
    };

    this.nextBehavior = function() {
        nextBehaviorIndex = this.behaviorIndex + 1;
        $("#avatarSpeechAudio" + nextBehaviorIndex).trigger('load');
        $("#avatarGesture" + this.behaviorIndex).hide();
        $("#avatarLine" + this.behaviorIndex).hide();
        $("#avatarHtml" + this.behaviorIndex).hide();
        $("#avatarSpeech" + this.behaviorIndex).hide();

        $("#avatarGesture" + nextBehaviorIndex).show();
        $("#avatarLine" + nextBehaviorIndex).show();
        $("#avatarHtml" + nextBehaviorIndex).show();
        $("#avatarSpeech" + nextBehaviorIndex).show();
        $("#avatarSpeechAudio" + nextBehaviorIndex).trigger('play');
        if($("#avatarGestureVideo" + nextBehaviorIndex).get(0)){
            $("#avatarGestureVideo" + nextBehaviorIndex).get(0).play();
        }
        this.setTimer(this.timers[nextBehaviorIndex]);
        this.behaviorIndex = nextBehaviorIndex;
    };

    this.isLastBehavior = function() {
        if(this.behaviorIndex >= this.numberOfSlides-1){
            return true;
        } else {
            return false;
        }
    }

    this.canEnter = function(){
        return this.enterToContinue[this.behaviorIndex];
    }
}


/*
$('#mute_speech').on('click', function(){
    $("#speech").prop("muted",!$("#speech").prop("muted"));
});
*/
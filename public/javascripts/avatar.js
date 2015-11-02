var AvatarBehaviorManager = function (numberOfSlides) {
    this.numberOfSlides = numberOfSlides;
    this.behaviorIndex = 0;
    this.activeBehavior = 0;
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
    this.choice = [false, 0, 0];

    this.start = function() {
        this.behaviorIndex = 0;
        this.activeBehavior = 0;
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
        if(this.choice[0]){
            this.behaviorIndex += this.choice[2];
        } else {
            this.behaviorIndex++;
        }
        $("#avatarSpeechAudio" + this.behaviorIndex).trigger('load');
        $("#avatarGesture" + this.activeBehavior).hide();
        $("#avatarLine" + this.activeBehavior).hide();
        $("#avatarHtml" + this.activeBehavior).hide();
        $("#avatarSpeech" + this.activeBehavior).hide();
        $("#avatarSpeechAudio" + this.activeBehavior).trigger('pause');

        $("#avatarGesture" + this.behaviorIndex).show();
        $("#avatarLine" + this.behaviorIndex).show();
        $("#avatarHtml" + this.behaviorIndex).show();
        $("#avatarSpeech" + this.behaviorIndex).show();
        $("#avatarSpeechAudio" + this.behaviorIndex).trigger('play');
        if($("#avatarGestureVideo" + this.behaviorIndex).get(0)){
            $("#avatarGestureVideo" + this.behaviorIndex).get(0).play();
        }
        if($(".avatarInputFocus" + this.behaviorIndex).get(0)){
            $(".avatarInputFocus" + this.behaviorIndex).focus();
        }
        this.activeBehavior = this.behaviorIndex;
        this.setTimer(this.timers[this.behaviorIndex]);
        if(this.choice[0]){
            this.behaviorIndex += (this.choice[1] - this.choice[2]);
            this.choice = [false, 0, 0];
        } else {
            this.choice = [false, 0, 0];
        }
    };

    this.setChoice = function(numberOfChoices, choiceIndex) {
        this.choice = [true, numberOfChoices, choiceIndex];
    }

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
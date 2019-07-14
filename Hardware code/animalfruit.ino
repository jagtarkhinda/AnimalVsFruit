// This #include statement was automatically added by the Particle IDE.
#include <InternetButton.h>

InternetButton b = InternetButton();

bool lightsColorChanged = false;

void setup() {

    // 1. Setup the Internet Button
    b.begin();
    Particle.function("colors", controlColor);
}

void loop(){
    
    if(b.buttonOn(2)){
        Particle.publish("broadcast","1",60,PUBLIC);
        delay(500);
    }
    
    if(b.buttonOn(4)){
        Particle.publish("broadcast","2",60,PUBLIC);
        delay(500);
    }
    
    if(b.buttonOn(3)){
        Particle.publish("broadcast","3",60,PUBLIC);
        delay(500);
    }
}

int controlColor(String command){
    
    Particle.publish("ccc",command);
    if(command == "red")
    {
        b.allLedsOn(150,0,0);
        delay(1000);
        b.allLedsOff();
    }
    else if (command == "green")
    {
        b.allLedsOn(0,153,51);
        delay(1000);
        b.allLedsOff();
    }
    
    return 1;
}

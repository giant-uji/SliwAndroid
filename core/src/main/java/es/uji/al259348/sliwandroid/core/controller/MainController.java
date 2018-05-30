package es.uji.al259348.sliwandroid.core.controller;

public interface MainController extends Controller {

    void decideStep();
    void registerDevice();
    void link();
    void unlink();
    void takeSample();
    void takeValidSample(String location);

}

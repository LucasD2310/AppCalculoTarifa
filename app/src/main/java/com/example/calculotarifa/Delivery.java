package com.example.calculotarifa;

public class Delivery {
    private int price;
    private float distance;

    public Delivery(int price, float distance){
        this.price=price;
        this.distance=distance;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int calculateDispatch(){
        // Definir variable a retornar
        int dispatch=0;
        // Aplicar lógica de negocio
        if (price > 50000 && distance <= 20){
            dispatch = 0;
        }
        else if(price >= 25000 && price <= 49990){
            dispatch = Math.round((distance) * 150);
        }
        else if(price < 25000){
            dispatch = Math.round((distance) * 300);
        }

        return dispatch;
    }
}

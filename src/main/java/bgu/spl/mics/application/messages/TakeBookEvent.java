package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class TakeBookEvent implements Event {
    String bookName;
    int moneyLeft;

    public TakeBookEvent(String bookName, int moneyLeft ){
        this.bookName = bookName;
        this.moneyLeft = moneyLeft;
    }

    public int getMoneyLeft() {
        return moneyLeft;
    }

    public String getBookName() {
        return bookName;
    }
}

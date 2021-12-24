package de.offene_pflege.op.threads;

import de.offene_pflege.op.OPDE;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 05.03.13
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
@Log4j2
public class MessageQ {
    private List<DisplayMessage> messages;

    public MessageQ() {
        this.messages = Collections.synchronizedList(new ArrayList<DisplayMessage>());
    }

    public synchronized void clear() {
        synchronized (messages) {
            messages.clear();
        }
    }

    public synchronized void add(DisplayMessage message) {
        synchronized (messages) {
            messages.add(message);
            Collections.sort(messages);
        }
    }

    public synchronized void next() {
        synchronized (messages) {
            messages.remove(0);
        }
    }

    public synchronized boolean isEmpty() {
        boolean isEmpty;
        synchronized (messages) {
            isEmpty = messages.isEmpty();
        }
        return isEmpty;
    }

    public synchronized boolean hasNextMessage() {
        boolean b;
        synchronized (messages) {
            b = messages.size() > 1;
        }
        return b;
    }

    public synchronized DisplayMessage getHead() {
        DisplayMessage head = null;
        synchronized (messages) {
            if (!messages.isEmpty()) {
                head = messages.get(0);
            }
        }
        return head;
    }

    public synchronized DisplayMessage getNextMessage() {
        DisplayMessage nextMessage = null;
        synchronized (messages) {
            if (messages.size() > 1) {
                nextMessage = messages.get(1);
            }
        }
        return nextMessage;
    }

    public synchronized void debug() {
        synchronized (messages) {
            if (messages.isEmpty()) {
                log.debug("messageQ empty");
            } else {
                for (DisplayMessage msg : messages) {
                    log.debug(msg);
                }
            }
        }
    }

}

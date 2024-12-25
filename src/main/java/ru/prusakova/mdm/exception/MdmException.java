package ru.prusakova.mdm.exception;

public class MdmException extends RuntimeException {

    public MdmException(String msg) {
        super(msg);
    }

    public MdmException(String msg, Exception e) {
        super(msg, e);
    }
}

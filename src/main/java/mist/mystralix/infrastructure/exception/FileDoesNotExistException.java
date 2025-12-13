package mist.mystralix.infrastructure.exception;

public class FileDoesNotExistException extends FileException {

    public FileDoesNotExistException(String message) {
        super(message);
    }
}
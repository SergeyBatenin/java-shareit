package ru.practicum.shareit.booking;

public enum BookingState {
    ALL, // все
    CURRENT, // текущие
    PAST, // завершённые
    FUTURE, // будущие
    WAITING, // ожидающие подтверждения
    REJECTED; // отклонённые

    public static BookingState from(String value) {
        for (BookingState state : BookingState.values()) {
            if (state.name().equalsIgnoreCase(value)) {
                return state;
            }
        }
        return null;
    }
}

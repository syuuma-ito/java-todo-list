function toApiDateTime(value) {
    const match = value.match(
        /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})(?::(\d{2}))?(?:\.(\d{1,3}))?$/,
    );

    if (!match) {
        throw new Error("期限の形式が不正です");
    }

    const [, year, month, day, hour, minute, second = "00", milliseconds = "0"] =
        match;

    return `${year}-${month}-${day}T${hour}:${minute}:${second}.${milliseconds.padEnd(3, "0")}`;
}

function toDateTimeLocal(value) {
    return value ? value.slice(0, 19) : "";
}

function formatDateTime(value) {
    const match = value?.match(
        /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})/,
    );

    if (!match) {
        return "日時不明";
    }

    const [, year, month, day, hour, minute] = match;
    return `${year}/${month}/${day} ${hour}:${minute}`;
}

function isOverdue(value) {
    const match = value?.match(
        /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})(?::(\d{2}))?/,
    );

    if (!match) {
        return false;
    }

    const [, year, month, day, hour, minute, second = "0"] = match;
    const deadline = new Date(
        Number(year),
        Number(month) - 1,
        Number(day),
        Number(hour),
        Number(minute),
        Number(second),
    );

    return deadline.getTime() < Date.now();
}

export { formatDateTime, isOverdue, toApiDateTime, toDateTimeLocal };

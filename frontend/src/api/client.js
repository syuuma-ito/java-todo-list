class ApiError extends Error {
    constructor(message, status = 0, errors = [], options) {
        super(message, options);
        this.name = "ApiError";
        this.status = status;
        this.errors = errors;
    }
}

async function request(path, { body, headers, ...options } = {}) {
    let response;

    try {
        response = await fetch(path, {
            ...options,
            credentials: "omit",
            headers: {
                Accept: "application/json",
                ...(body === undefined ? {} : { "Content-Type": "application/json" }),
                ...headers,
            },
            body: body === undefined ? undefined : JSON.stringify(body),
        });
    } catch (error) {
        if (error.name === "AbortError") {
            throw error;
        }

        throw new ApiError("サーバーに接続できません", 0, [], { cause: error });
    }

    let result;

    try {
        result = await response.json();
    } catch (error) {
        throw new ApiError(
            "サーバーから正しい形式の応答を受け取れませんでした",
            response.status,
            [],
            { cause: error },
        );
    }

    if (
        typeof result !== "object" ||
        result === null ||
        typeof result.ok !== "boolean" ||
        !Array.isArray(result.errors) ||
        !Object.hasOwn(result, "data")
    ) {
        throw new ApiError("APIの応答形式が不正です", response.status);
    }

    if (!response.ok || !result.ok) {
        const message = result.errors
            .map((error) => error?.message)
            .filter(Boolean)
            .join("\n");

        throw new ApiError(
            message || `APIリクエストに失敗しました（${response.status}）`,
            response.status,
            result.errors,
        );
    }

    return result.data;
}

export { ApiError, request };

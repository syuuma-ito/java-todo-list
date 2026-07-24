const TASK_TYPE = {
    NORMAL: "NORMAL",
    DEADLINE: "DEADLINE",
};

const TASK_TYPE_LABEL = {
    [TASK_TYPE.NORMAL]: "通常タスク",
    [TASK_TYPE.DEADLINE]: "期限付きタスク",
};

const PRIORITY = {
    NONE: "NONE",
    LOW: "LOW",
    MEDIUM: "MEDIUM",
    HIGH: "HIGH",
};

const PRIORITY_LABEL = {
    [PRIORITY.NONE]: "指定なし",
    [PRIORITY.LOW]: "低",
    [PRIORITY.MEDIUM]: "中",
    [PRIORITY.HIGH]: "高",
};

const COLORS = [
    { name: "ピンク", value: "#F6C7D4" },
    { name: "オレンジ", value: "#FFD3BF" },
    { name: "黄色", value: "#F8E6A0" },
    { name: "黄緑", value: "#D7E9B9" },
    { name: "緑", value: "#BDE5D5" },
    { name: "水色", value: "#BDE0F2" },
    { name: "青", value: "#C6D4F2" },
    { name: "紫", value: "#D8C9EE" },
    { name: "赤紫", value: "#E6C8E4" },
    { name: "ベージュ", value: "#E5D4C3" },
];

export { COLORS, PRIORITY, PRIORITY_LABEL, TASK_TYPE, TASK_TYPE_LABEL };

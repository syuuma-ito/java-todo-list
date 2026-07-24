# タスク管理アプリ

## 開発環境

フロントエンドの実行環境:
- Node.js: 26.5.0 (それと 24.5.0)
- npm: 11.17.0 (それと 11.12.1)

バックエンドの実行環境:
- Java: 21.0.7
- Maven: 3.6.3

で動作確認をしています。

## 起動方法

バックエンドとフロントエンドは、別々のターミナルで起動する。

### バックエンド

```bash
cd backend
mvn spring-boot:run
```

バックエンドは、`http://localhost:8080`で起動する。

### フロントエンド

依存関係のインストール

```bash
cd frontend
npm install
```

サーバーの起動

```bash
npm run dev
```

ブラウザで`http://localhost:5173`から見ることができます。

### 終了方法

バックエンドとフロントエンドを起動している各ターミナルで、`Ctrl+C`を入力する。

## フロントエンド

技術:
- React
- Vite

使った理由:
- Reactは個人的に好きで、慣れているため
- バックエンド側はjavaで作るため、Next.jsでは過剰なのでViteを使った。

## バックエンド

技術:
- Java
- Spring

## タスクの扱いについて

- 通常タスク
- 期限付きタスク

を持つ

### それぞれの要素

#### TaskBase

| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| id | UUID | string | null不可 |
| type | enum | string | null不可、NORMAL、DEADLINE のいずれか |
| name | String | string | null不可 |
| details | String | string | 空文字可、null不可 |
| color | String | string | 空文字不可、null不可 |
| priority | Priority | string | null不可、LOW、MEDIUM、HIGH のいずれか |
| completed | bool | boolean | null不可 |
| order | int | number | null不可、0以上 |

#### NormalTask

TaskBaseを継承

#### DeadlineTask

TaskBaseを継承

| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| deadline | LocalDateTime | string | null不可、yyyy-MM-dd'T'HH:mm:ss.SSS |

## タスクの管理について

### TaskList

| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| id | UUID | string | null不可 |
| name | String | string | null不可 |
| description | String | string | 空文字可、null不可 |
| tasks | List<Task> | array | null不可、空配列可 |
| order | int | number | null不可、0以上 |

# APIルーティング

### GET /api/task-lists

すべてのタスクリストを取得する。

返り値: TaskListの配列

### GET /api/task-lists/{taskListId}

指定したタスクリストを取得する。

返り値: TaskList

### POST /api/task-lists

新しいタスクリストを作成する。
idはサーバー側で指定
orderはサーバー側で指定し、末尾に追加する。

リクエストボディ:

| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| name | String | string | null不可 |
| description | String | string | 空文字可、null不可 |

返り値: 変更後のTaskList

ステータスコード: 201 Created

### PUT /api/task-lists/{taskListId}

指定したタスクリストを更新する。

リクエストボディ:

| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| name | String | string | null不可 |
| description | String | string | 空文字可、null不可 |

返り値: 変更後のTaskList

ステータスコード: 200 OK

### DELETE /api/task-lists/{taskListId}

指定したタスクリストを削除する。
関連しているタスクも削除される。

リクエストボディ: なし

返り値:

| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| id | UUID | string | null不可 |

削除されたタスクリストのidを返す。

ステータスコード: 200 OK

### PUT /api/task-lists/order

タスクリストの表示順を更新する。
すべてのタスクリストのidを表示順に指定する。

リクエストボディ:

| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| taskListIds | List\<UUID> | array | null不可、すべてのidを重複なく指定 |

返り値: なし

## タスク

### POST /api/task-lists/{taskListId}/tasks

指定したタスクリストに新しいタスクを作成する。
idはサーバー側で生成
orderはサーバー側で設定し、指定したタスクリスト内の末尾に追加する。

リクエストボディ:

| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| type | enum | string | null不可、NORMAL、DEADLINE のいずれか |
| name | String | string | null不可 |
| details | String | string | 空文字可、null不可 |
| color | String | string | 空文字不可、null不可 |
| priority | Priority | string | null不可、LOW、MEDIUM、HIGH のいずれか |
| completed | bool | boolean | null不可 |

DeadlineTaskの場合:

| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| deadline | LocalDateTime | string | null不可、yyyy-MM-dd'T'HH:mm:ss.SSS |

返り値: 変更後のTask

ステータスコード: 201 Created

### PUT /api/task-lists/{taskListId}/tasks/{taskId}

指定したタスクを更新する。
タスクのtypeは変更しない。

リクエストボディ:

| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| name | String | string | null不可 |
| details | String | string | 空文字可、null不可 |
| color | String | string | 空文字不可、null不可 |
| priority | Priority | string | null不可、LOW、MEDIUM、HIGH のいずれか |
| completed | bool | boolean | null不可 |

DeadlineTaskの場合:

| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| deadline | LocalDateTime | string | null不可、yyyy-MM-dd'T'HH:mm:ss.SSS |

返り値: 変更後のTask

ステータスコード: 200 OK

### GET /api/task-lists/{taskListId}/tasks/{taskId}

指定したタスクを取得する。

返り値: Task

### DELETE /api/task-lists/{taskListId}/tasks/{taskId}

指定したタスクを削除する。

リクエストボディ: なし

返り値:
| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| id | UUID | string | null不可 |

削除されたタスクのidを返す。

ステータスコード: 200 OK

### PUT /api/task-lists/{taskListId}/tasks/order

タスクの表示順を更新する。
指定したタスクリストにあるすべてのタスクのidを表示順に指定する。

リクエストボディ:
| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| taskIds | List\<UUID> | array | null不可、すべてのidを重複なく指定 |

返り値: なし

### POST /api/task-lists/{taskListId}/tasks/{taskId}/complete

タスクの完了フラグを更新する。

リクエストボディ:

| 項目 | Java型 | JSON型 | 制約 |
| --- | --- | --- | --- |
| completed | bool | boolean | null不可 |

返り値: 変更後のTask

## エラー

エラーコード
- 400: リクエストのパラメータが不正な場合
- 404: 指定したタスクリスト、タスクが存在しない場合
- 500: サーバー内部でエラーが発生した場合

形式は以下のようにする

```json
{
    "status": 400,
    "ok": false,
    "errors": [
        {
            "type": "ValidationError",
            "message": "priorityはLOW、MEDIUM、HIGHのいずれかを指定してください"
        }
    ],
    "data": null
}
```

正常の場合は以下のようにする

```json
{
    "status": 200,
    "ok": true,
    "errors": [],
    "data": {
        "内容": "ここに返却するデータを入れる"
    }
}
```

# フロントエンドについて

## デザイン

- 全体は、シンプルでかわいい、ニューモフィズムデザインを使う (単純な自分の好みです)
- ライトモードで、ダークモードは実装しない

# AIの使用について

この課題を作成する際に、以下の部分でAIを使用しました。

- 設計の壁打ちなどの相談
- Spring Bootの仕様や、使い方の相談
- フロントエンドのコードの一部 (コンポーネントや、CSSなど)

使用したAIモデルは、GPT-5.6-sol highです。
Javaのコード自体はAIを利用せず書いています

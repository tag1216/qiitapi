# qiitapi

Qiita API のJavaバッチ向け読み込み専用ラッパーです。

- 配列を返すAPIではページングの処理は隠蔽しているため、二重のループを書く必要がありません。
- リクエスト毎にウェイトを入れているので、連続リクエストで頻繁に発生するサーバーエラーを回避しています。
- サーバーエラーが発生した際は、可能な場合はリトライを行います。

新着投稿を10,000件取得するには次のように書きます。(100件ずつ、100ページ取得)

```java
QiitaClient client = QiitaClient.builder().build();
List<Item> items = client.items()
        .perPage(100)
        .limit(100)
        .stream()
        .collect(toList());
```

## 使用方法

### QiitaClientの作成
```java
QiitaClient client = QiitaClient.builder().build();
```

### アクセストークンの設定
```java
QiitaClient client = QiitaClient.builder()
        .accessToken("3c18d08d88f2b9eb06d40b51606fa3b11ae35263")
        .build();
```

### デフォルトページネーションの設定
```java
QiitaClient client = QiitaClient.builder()
        .defaultPerPage(100)
        .defaultPageLimit(100)
        .build();
```

### リクエスト前の待機時間(ms)の設定
```java
QiitaClient client = QiitaClient.builder()
        .requestWait(200)
        .build();
```

### サーバーエラー発生時のリトライ回数の設定
```java
QiitaClient client = QiitaClient.builder()
        .retryLimit(10)
        .build();
```

### 新着投稿一覧の取得
```java
List<Item> items = client.items()
        .stream()
        .collect(toList());
```

### 件数の取得
```java
int count = client.items().totalCount();
```



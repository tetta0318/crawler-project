# crawler-project

指定した URL のページをクロールし、HTML と付随するリソース（CSS / JavaScript / 画像）をローカルに保存する Java 製のツールです。保存した HTML はリソースへの参照をローカルパスに書き換えるため、オフラインでもブラウザから開けます。

## 特徴

- 起点 URL からリンクをたどって再帰的にクロール（既定で深さ 2）
- `<link rel=stylesheet>` / `<script src>` / `<img>` / インライン `style` の `url(...)` を抽出してダウンロード
- ダウンロードは固定スレッドプール（16 スレッド）で並列実行。クロール本体はダウンロード完了を待たずに次のページへ進む
- 訪問済みページ・取得済みリソースを記録し、同じ URL の重複取得を回避
- ページ内リンクは、保存した HTML 同士の相対パスに書き換え

## 必要環境

- JDK 21（Gradle toolchain が対応 JDK を自動で解決します）
- Gradle Wrapper 同梱のため Gradle のインストールは不要

主な依存ライブラリ:

| ライブラリ | 用途 |
| --- | --- |
| [jsoup](https://jsoup.org/) 1.22.2 | HTML の取得・パース・書き換え |
| Guava 33.5.0-jre | ユーティリティ |
| JUnit Jupiter 6.0.1 | テスト（テストランナー設定のみ） |

## 使い方

起点 URL を引数に渡して実行します。

```bash
# macOS / Linux
./gradlew run --args="https://example.com"
```

```powershell
# Windows
.\gradlew.bat run --args="https://example.com"
```

ビルドのみ行う場合:

```bash
./gradlew build
```

## 出力

実行ディレクトリ直下の `output/` にページ単位のディレクトリが作られます。ディレクトリ名は `<title>` から、ファイル名として使えない文字（`\ / : * ? " < > |`）を `_` に置換したものです。

```
output/
└── <ページタイトル>/
    ├── 1.html      … 保存した HTML
    ├── css/1.css   … スタイルシート
    ├── js/1.js     … JavaScript
    └── img/1.png   … 画像
```

リソースのファイル名はページごとの連番です。拡張子は原則リソース種別名をそのまま使いますが、画像だけは実体が分からずブラウザで開けなくなるため URL から拡張子を取り出します（取得できない場合は `.img`）。

## クラス構成

| クラス | 役割 |
| --- | --- |
| `CrawlerMain` | エントリポイント。スレッドプールの生成とクロール後の待ち合わせ |
| `Crawler` | 1 ページ分の処理（取得 → リソース抽出 → リンクを再帰的にクロール → HTML 保存） |
| `HtmlResourceExtractor` | HTML からリソース URL を抽出し、保存先を決めて参照を書き換える |
| `LinkExtracter` | `a[href]` から追跡対象リンクを抽出 |
| `ResourceDownloader` | URL と保存先を受け取ってダウンロードを実行（状態を持たずスレッド間で共有） |
| `DownloadManager` | 訪問済みページ / 取得済みリソースの URL とローカルパスの対応を保持 |
| `PathResolver` | ページ単位の出力先ディレクトリ・ファイル名・相対参照パスの決定 |
| `DirCreater` | ディレクトリ作成 |
| `UrlUtils` | 拡張子抽出、CSS の `url(...)` 抽出、URL のハッシュ化 |
| `Logger` | `synchronized` なコンソールログ（複数スレッドからのログ混在を防ぐ） |

## 制限事項

- クロール深度は `CrawlerMain` からの `Crawler#crawl` 呼び出しにハードコードされています。同様にスレッド数は `CrawlerMain.THREAD_COUNT`、出力先は `output` 固定です。
- 外部ドメインへのリンクも制限なくたどります。
- CSS ファイル内から参照される画像は取得しません（`CssResourceExtractor` はコメントアウトされた未完成のコードです）。
- `#` を含む URL はページ・リソースともにスキップします。
- robots.txt の解釈やクロール間隔の制御は行いません。**対象サイトの利用規約と robots.txt を確認し、過度な負荷をかけないようご自身の責任でご利用ください。**
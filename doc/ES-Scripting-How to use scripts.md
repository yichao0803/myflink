# How to use scripts

在 Elasticsearch API 中支持脚本的任何地方，语法都遵循相同的模式：

```json
 "script": {
    "lang":   "...",  
    "source" | "id": "...", 
    "params": { ... } 
  }
```

1. 脚本使用的语言，默认为 `painless`。

2. 可以 `source` 为内联脚本或id存储脚本指定脚本本身。

3. 应该传递给脚本的任何命名参数。

例如，在搜索请求中使用以下脚本来返回 [脚本字段](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/search-request-script-fields.html)：

```json 
PUT my_index/_doc/1
{
  "my_field": 5
}

GET my_index/_search
{
  "script_fields": {
    "my_doubled_field": {
      "script": {
        "lang":   "expression",
        "source": "doc['my_field'] * multiplier",
        "params": {
          "multiplier": 2
        }
      }
    }
  }
}
```

### 脚本参数

- **`lang`**

  指定编写脚本的语言。默认为`painless`。

- **`source`， `id`**

  指定脚本的来源。如上例所示，`inline`指定了一个脚本 `source`。`stored`指定了一个脚本，`id` 并从集群状态中检索了该[脚本](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/modules-scripting-using.html#modules-scripting-stored-scripts)（请参阅“ [存储的脚本”](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/modules-scripting-using.html#modules-scripting-stored-scripts)）。

- **`params`**

  指定作为变量传递到脚本中的所有命名参数。



###  首选参数

Elasticsearch 第一次看到新脚本时，将对其进行编译并将编译后的版本存储在缓存中。编译可能是一个繁重的过程。

如果需要将变量传递到脚本中，则应以命名形式传递变量，`params`而不是将值硬编码到脚本本身中。例如，如果您希望能够将字段值乘以不同的乘数，请不要将乘数硬编码到脚本中：

```json
"source": "doc['my_field'] * 2"
```

而是将其作为命名参数传递：

```json
  "source": "doc['my_field'] * multiplier",
  "params": {
    "multiplier": 2
  }
```

每当乘数发生变化时，都必须重新编译第一个版本。第二个版本仅编译一次。

如果您在短时间内编译太多唯一脚本，Elasticsearch将拒绝新动态脚本，并显示 `circuit_breaking_exception`错误。默认情况下，每分钟最多可编译15个内联脚本。您可以通过设置来动态更改此设置 `script.max_compilations_rate`。

### 简短的脚本形式

为了简短起见，可以使用简短的脚本形式。简而言之，`script`用字符串而不是对象来表示。此字符串包含脚本的源。

简写：

```json
 "script": "ctx._source.likes++"
```

相同格式的普通脚本：

```json
  "script": {
    "source": "ctx._source.likes++"
  }
```

### 存储的脚本

可以使用`_scripts`端点将脚本存储在群集状态中或从群集状态中检索脚本 。

#### 索取范例

以下是使用位于的存储脚本的示例 `/_scripts/{id}`。

首先，创建`calculate-score`在集群状态下调用的脚本：

```json
POST _scripts/calculate-score
{
  "script": {
    "lang": "painless",
    "source": "Math.log(_score * 2) + params.my_modifier"
  }
}
```

可以使用以下方式检索相同的脚本：

```json
GET _scripts/calculate-score
```

可以通过指定以下`id`参数来使用存储的脚本：

```json
GET _search
{
  "query": {
    "script": {
      "script": {
        "id": "calculate-score",
        "params": {
          "my_modifier": 2
        }
      }
    }
  }
}
```

删除脚本：

```console
DELETE _scripts/calculate-score
```

### 搜索模板

您还可以使用`_scripts`API来存储**搜索模板**。搜索模板使用占位符值（称为模板参数）保存特定的[搜索请求](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/search-search.html)。

您可以使用存储的搜索模板来运行搜索，而无需写出整个查询。只需提供存储的模板的ID和模板参数即可。当您要快速且无错误地运行常用查询时，此功能很有用。

搜索模板使用 [mustache templating language](http://mustache.github.io/mustache.5.html)。有关更多信息和示例，请参见[*搜索模板*](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/search-template.html)。

### 脚本缓存

默认情况下，所有脚本都是高速缓存的，因此仅在发生更新时才需要重新编译它们。默认情况下，脚本没有基于时间的到期，但是您可以通过使用`script.cache.expire`设置来更改此行为。您可以使用该`script.cache.max_size`设置来配置此缓存的大小。默认情况下，缓存大小为`100`。

脚本的大小限制为65,535字节。可以通过设置`script.max_size_in_bytes`来增加该软限制来更改此设置，但是如果脚本确实很大，则应考虑使用 [本机脚本引擎](https://www.elastic.co/guide/en/elasticsearch/reference/6.8/modules-scripting-engine.html)。




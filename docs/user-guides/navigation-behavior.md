# Navigation Behavior Guide

## App Start Behavior

### With Recent Playback

- App launches directly to **PlayTopicView**
- Last played topic is automatically loaded

### Without Recent Playback

- App launches to **TopicListView**
- User can select a topic to play

## Back Button Actions

### TopicListView

- **With recent playback**: Navigate to PlayTopicView after 500ms delay
- **Double back (within 500ms)**: Exit app (priority over navigation)
- **No recent playback**: Double back within 500ms to exit app

### PlayTopicView

- **Back button**: Return to TopicListView immediately

### AccountView

- **Back button**: Return to TopicListView immediately

## Navigation Flow

### Basic Navigation

- TopicListView → EditTopicView
- TopicListView → PlayTopicView
- TopicListView → AccountView

### Back Button Circular Navigation

```plaintext
TopicListView ↔ PlayTopicView
     ↑
AccountView
```

### Key Features

- **Circular navigation** between TopicList and PlayTopic when recent playback exists
- **Stack management** prevents infinite growth using `popUpTo`
- **Double-back priority** over circular navigation ensures clean app exit

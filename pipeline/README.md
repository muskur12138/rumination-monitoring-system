# 牛反刍行为监测流水线（代码节选）

本仓库对应论文 *Development of an automatic monitoring system for cattle rumination behavior based on solid conduction and deep learning*。

## 说明

本仓库仅提供**部分代码**，用于展示流水线的基本流程（音频读取、梅尔频谱生成示例）。

完整代码暂未公开。项目仍处于持续开发阶段、代码尚未定型，为避免误导使用者，这里仅提供代码节选。未公开的核心功能包括：

- 神经网络降噪模块（瓶颈自编码器）
- YOLOv11 咀嚼事件检测
- 反刍周期统计规则（3 s 间隔合并）
- 训练权重文件

如需完整代码，可联系通讯作者。

## 文件说明

| 文件 | 说明 |
| --- | --- |
| `audio_io.py` | 音频加载 / 保存工具（基于 soundfile） |
| `example_mel_spectrogram.py` | 示例：音频 → 梅尔频谱 |
| `requirements.txt` | 依赖清单 |

## 流程概览

原始音频 → 预处理（带通滤波 / 基线校正）→ 神经网络降噪 → 梅尔频谱 → YOLOv11 检测 → 周期统计

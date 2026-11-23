"""
audio_io.py —— 音频加载与保存

使用 soundfile（libsndfile）原生支持 WAV / FLAC / OGG / MP3 读取，
统一返回 float32 波形（取值范围约 [-1, 1]）与采样率。
"""
from __future__ import annotations

import numpy as np
import soundfile as sf

SUPPORTED_EXTS = ("wav", "flac", "ogg", "mp3", "aif", "aiff", "w64", "caf", "au")


def load_audio(path: str):
    """加载音频 -> (waveform_float32, sample_rate)。

    waveform 形状：(n,) 单声道 或 (n, channels) 多声道。
    """
    data, sr = sf.read(path, dtype="float32", always_2d=False)
    return data, int(sr)


def save_wav(path: str, x: np.ndarray, sr: int):
    """保存为 16-bit WAV（自动软限幅防止爆音）。"""
    y = np.asarray(x, dtype=np.float32)
    y = np.clip(y, -1.0, 1.0)
    sf.write(path, y, int(sr), subtype="PCM_16")


def to_mono(x: np.ndarray) -> np.ndarray:
    """多声道 -> 单声道（按通道平均）。"""
    x = np.asarray(x, dtype=np.float32)
    if x.ndim == 1:
        return x
    return x.mean(axis=1)

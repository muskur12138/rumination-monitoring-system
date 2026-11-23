"""
example_mel_spectrogram.py —— 示例：音频 → 梅尔频谱

演示流水线的第一步（音频读取与梅尔频谱生成），使用标准 librosa。
核心的降噪、YOLO 检测与周期统计逻辑未包含在本示例中。

用法：python example_mel_spectrogram.py <音频文件>
"""
import sys

import librosa
import librosa.display
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

from audio_io import load_audio


def main(audio_path: str):
    y, sr = load_audio(audio_path)

    # 梅尔频谱（示例参数，非生产参数）
    S = librosa.feature.melspectrogram(y=y, sr=sr, n_fft=2048, hop_length=512, n_mels=128)
    S_db = librosa.power_to_db(S, ref=S.max())

    plt.figure(figsize=(10, 4))
    librosa.display.specshow(S_db, sr=sr, hop_length=512, x_axis="time", y_axis="mel", cmap="magma")
    plt.colorbar(format="%+2.0f dB")
    plt.title("Mel spectrogram (example)")
    plt.tight_layout()
    plt.savefig("example_mel.png", dpi=150)
    print("已保存 example_mel.png")


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("用法: python example_mel_spectrogram.py <音频文件>")
    else:
        main(sys.argv[1])

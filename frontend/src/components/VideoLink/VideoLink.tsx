import type { Video, VideoPlatform } from "../../api/types";
import "./VideoLink.css";

const PLATFORM_LABEL: Record<VideoPlatform, string> = {
  YOUTUBE: "YouTube",
  INSTAGRAM: "Instagram",
  OTHER: "Video",
};

function PlatformIcon({ platform }: { platform: VideoPlatform }) {
  switch (platform) {
    case "YOUTUBE":
      return (
        <svg viewBox="0 0 24 24" aria-hidden="true" className="video-link__icon">
          <rect x="1" y="4.5" width="22" height="15" rx="4" fill="currentColor" />
          <path d="M10 8.5l6 3.5-6 3.5z" fill="var(--color-surface)" />
        </svg>
      );
    case "INSTAGRAM":
      return (
        <svg viewBox="0 0 24 24" aria-hidden="true" className="video-link__icon">
          <rect x="2" y="2" width="20" height="20" rx="6" fill="currentColor" />
          <circle cx="12" cy="12" r="5" fill="none" stroke="var(--color-surface)" strokeWidth="1.8" />
          <circle cx="17.4" cy="6.6" r="1.3" fill="var(--color-surface)" />
        </svg>
      );
    default:
      return (
        <svg viewBox="0 0 24 24" aria-hidden="true" className="video-link__icon">
          <circle cx="12" cy="12" r="11" fill="currentColor" />
          <path
            d="M10 8.5l6 3.5-6 3.5z"
            fill="var(--color-surface)"
          />
        </svg>
      );
  }
}

interface VideoLinkProps {
  video: Video;
}

export function VideoLink({ video }: VideoLinkProps) {
  return (
    <a
      className={`video-link video-link--${video.platform.toLowerCase()}`}
      href={video.url}
      target="_blank"
      rel="noopener noreferrer"
    >
      <PlatformIcon platform={video.platform} />
      <span className="video-link__label">{PLATFORM_LABEL[video.platform]}</span>
      <span className="video-link__url">{video.url}</span>
      <svg className="video-link__external" viewBox="0 0 20 20" aria-hidden="true">
        <path
          d="M7 5h8v8M15 5 5 15"
          fill="none"
          stroke="currentColor"
          strokeWidth="1.6"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </svg>
    </a>
  );
}

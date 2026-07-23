import { cn } from "@/lib/utils"

function Skeleton({
  className,
  ...props
}) {
  return (
    <div
      data-slot="skeleton"
      className={cn(
        "animate-pulse rounded-xl border border-white/45 bg-muted/70 shadow-[var(--shadow-inset-soft)]",
        className
      )}
      {...props} />
  );
}

export { Skeleton }

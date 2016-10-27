# Change Log
All notable changes to this project will be documented in this file.

## Unversioned
### Added
- Selection rectangle to zoom in
- Zoom history (separate for mandelbrot and julia sets)
- Ability to go back in zoom history (backspace)
- Image caching for fast back
- Multithreaded slice-by-slice fractal generation (way faster)
- Boolean rendering readout that displays when fractal is rendering 

### Changed
- Updated instructions text
- General code cleanup

### Removed
- Render progress indicator, due to multithreading issues


## 1.2 - 2016-10-11
### Added
- Julia set rendering
- Ability to pick Julia set seed from the Mandelbrot image
- Jump back to Mandelbrot when in Julia mode


## 1.1 - 2016-07-27
### Added
- Color offset to all brushes
- Brush slider to control color offset
- Coordinate readout to show mouse coord

### Changed
- Complete brush revamp

### Removed
- TropicalBrush, as it was not interesting/different enough


## 1.0 - 2016-07-27
### Added
- Mandelbrot set rendering
- Ability to move around the set and zoom
- Five brushes to color the set differently
- Rendering progress indicator
- Zoom level indicator
- Max iteration selector and readout
- Ability to save images
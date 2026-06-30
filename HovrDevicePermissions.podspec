Pod::Spec.new do |s|
  s.name             = 'HovrDevicePermissions'
  s.version          = '1.2.8'
  s.summary          = 'HOVR native runtime permissions and connectivity monitoring.'
  s.description      = 'Pure-native location, notification, and network coordinators for HOVR rider apps.'
  s.homepage         = 'https://github.com/vishalsharma-hovr/hovr_device_permissions'
  s.license          = { :type => 'Proprietary' }
  s.author           = { 'HOVR' => 'dev@ridehovr.com' }
  s.source           = {
    :git => 'https://github.com/vishalsharma-hovr/hovr_device_permissions.git',
    :tag => "v#{s.version}",
  }
  s.ios.deployment_target = '16.0'
  s.swift_version    = '5.0'
  s.source_files     = 'ios/Sources/HovrDevicePermissions/**/*.swift'
end
